/*
 * Created on 06/10/2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.npa;

import java.sql.SQLException;
import java.util.*;
import java.sql.Date;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.npa.model.*;

/**
 * Classe di gestione delle funzionalita' per NPA
 *
 * @author Alex.Mancini
 */
public class NpaManager {


  static Logger logger = Logger.getLogger(NpaManager.class);

  String pattern_numerico = "(^100([.]0{1,2})?)$|(^\\d{1,2}([.]\\d{0,5})?)$";

  private SqlManager sqlManager;

  private NpaFunzioniManager    npaFunzioniManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setNpaFunzioniManager(NpaFunzioniManager npaFunzioniManager) {
    this.npaFunzioniManager = npaFunzioniManager;
  }

  private ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

  /*
 private static final String SQL_LAVORAZIONI_OFFERTE = "select count (distinct CONTAF) from V_GCAP_DPRE where  ngara=? and PREOFF is not null and isprodneg is null";
 private static final String SQL_LAVORAZIONI_OFFERTE_AGGIUDICATE = SQL_LAVORAZIONI_OFFERTE + " and QTAORDINATA is not null";
 private static final String SQL_VALUTAZIONE_PRODOTTI = "select count(*) from V_GARE_PRODOTTI_VALUTATI where ngara=?";
 private static final String SQL_VALUTAZIONE_PRODOTTI_AFFIDAMENTI = SQL_VALUTAZIONE_PRODOTTI + " and affidamento is not null";
 private static final String UPDATE_STATO_RICERCA =  "update gare set ISRICONCLUSA=?, DATCONCLUSA=?  where ngara=?";
   */

  public void registraJSON(Long idscheda, String codGar, String codiceScheda, String ngara, String tipoWSERP) throws Exception {
    TransactionStatus status=null;
    boolean doCommit = false;
    try {
      status = this.sqlManager.startTransaction();

      //verifico che la scheda sia ancora in stato 1 (gestione della concorrenza/disallineamento APPALTI-1975)
      Long stato = (Long) this.sqlManager.getObject("select statonpa from g1npascheda where id=?", new Object[]{idscheda});
      if(!new Long(1).equals(stato)){
        return;
      }

      if(((!npaFunzioniManager.isApplicazioneVersioneAttiva("3") && codiceScheda.startsWith("PL")) || "P1_15_1".equals(codiceScheda)) && Boolean.TRUE.equals(npaFunzioniManager.contieneEform(codiceScheda))) {

        //non fa nulla perche'queste schede non hanno il Json ma devono comunque mandare la richiesta
      } else {
        switch (codiceScheda) {
        case "A1_29":
        case "A1_30":
          registraJSON_A1_29(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A1_32":
          registraJSON_A1_32(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A1_33":
        case "A1_34":
          registraJSON_A1_33(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A1_35":
          registraJSON_A1_35(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A1_36":
        case "A1_37":
          registraJSON_A1_36(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A2_29":
        case "A2_30":
          registraJSON_A2_29(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A2_32":
          registraJSON_A2_32(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A2_33":
        case "A2_34":
          registraJSON_A2_33(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A2_35":
          registraJSON_A2_35(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A2_36":
        case "A2_37":
          registraJSON_A2_36(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A3_1":
          registraJSON_A3_1(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A3_2":
          registraJSON_A3_2(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A3_3":
          registraJSON_A3_3(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A3_6":
          registraJSON_A3_6(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A4_5":
          registraJSON_A4_5(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A7_1_1":
          registraJSON_A7_1_1(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "A7_1_2":
          registraJSON_A7_1_2(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "AD1_25":
        case "AD1_26":
          registraJSON_AD1_25(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "AD1_28":
          registraJSON_AD1_28(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "AD2_25":
        case "AD2_26":
          registraJSON_AD2_25(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "AD2_28":
          registraJSON_AD2_28(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "AD3":
          registraJSON_AD3(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "AD4":
          registraJSON_AD4(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "AD5":
          registraJSON_AD5(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "NAG":
          registraJSON_NAG(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_10":
          registraJSON_P1_10(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_11":
          registraJSON_P1_11(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_12":
        case "P1_13":
          registraJSON_P1_12(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_14":
          registraJSON_P1_14(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_16":
        case "P1_17":
          registraJSON_P1_16(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_19":
          registraJSON_P1_19(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_20":
        case "P1_21":
          registraJSON_P1_20(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_23":
          registraJSON_P1_23(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P1_24":
          registraJSON_P1_24(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_10":
          registraJSON_P2_10(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_11":
          registraJSON_P2_11(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_12":
        case "P2_13":
          registraJSON_P2_12(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_14":
          registraJSON_P2_14(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_16":
        case "P2_17":
          registraJSON_P2_16(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_19":
          registraJSON_P2_19(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_20":
        case "P2_21":
          registraJSON_P2_20(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_23":
          registraJSON_P2_23(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P2_24":
          registraJSON_P2_24(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P3_1":
          registraJSON_P3_1(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P3_2":
          registraJSON_P3_2(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P3_3":
          registraJSON_P3_3(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P4_5":
          registraJSON_P4_5(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P5":
          registraJSON_P5(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P6_1":
          registraJSON_P6_1(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P6_2":
          registraJSON_P6_2(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P7_1_1":
        case "P7_1_2":
        case "P7_1_3":
          registraJSON_P7_1_2(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P7_2":
          registraJSON_P7_2(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "P7_3":
          registraJSON_P7_3(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "PL2_1":
        case "PL2_2":
        case "PL2_7":
        case "PL2_8":
          registraJSON_PL2(idscheda, codGar, codiceScheda);
          break;
        case "S1":
          registraJSON_S1(idscheda, codGar, codiceScheda, ngara, tipoWSERP);
          break;
        case "S2":
          registraJSON_S2(idscheda, codGar, codiceScheda, ngara, tipoWSERP);
          break;
        case "S2R":
          registraJSON_S2R(idscheda, codGar, codiceScheda, ngara, tipoWSERP);
          break;
        case "S3":
          registraJSON_S3(idscheda, codGar, codiceScheda, tipoWSERP);
          break;
        case "ANN":
          registraJSON_ANN(codGar, codiceScheda);
          break;
        case "CM1":
        case "CM2":
          registraJSON_CM2(codGar,codiceScheda);
          break;
        case "PL1_1":
        case "PL1_2":
        case "PL1_4":
        case "PL1_5":
        case "PL1_7":
        case "PL1_8":
          registraJSON_PL1(codGar,codiceScheda);
          break;
        case "ID":
          registraJSON_ID(idscheda,codGar,codiceScheda);
          break;
        default:
          String message = "La scheda "+codiceScheda+" non risulta disponibile";
          throw new Exception(message);
        }
      }
      doCommit = true;
    } catch (Exception e) {
      logger.error("Errore durante la creazione del JSON per la scheda "+codiceScheda+", messaggio di errore: " + e.getMessage(), e );
      throw new GestoreException(e.getMessage(), "", e);
    } finally {
      if (status != null) {
        try {
          if (doCommit) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        } catch (SQLException ex) {
          throw new GestoreException("", "", ex);
        }
      }
    }
  }


  private void registraJSON_A3_1(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA3_1 anacFormA3_1 = new AnacFormA3_1();

    AppaltoA3_1 appaltoA3_1 = new AppaltoA3_1();
    appaltoA3_1.setModalitaRiaggiudicazioneAffidamento(null);
    appaltoA3_1.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));

    anacFormA3_1.setAppalto(appaltoA3_1);


    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA3_1> listaAggiudicazioneA3_1 = new ArrayList<AggiudicazioneA3_1>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA3_1 aggiudicazioneA3_1 = new AggiudicazioneA3_1();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Double limmax = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 6).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 47).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();

        if(codcig != null) {
          aggiudicazioneA3_1.setCig(codcig);
        }

        if(limmax != null) {
          //recupero il valore assoluto
          Double limmaxAssoluto = Math.abs(limmax);
          if(String.valueOf(limmaxAssoluto).matches(pattern_numerico)) {
            aggiudicazioneA3_1.setValoreSogliaAnomalia(limmaxAssoluto);
          }
        } else {
          //Errore pattern
        }
        
        //calcolo il quadro economico e il valmax tenendo conto della possibile presenza di un record in G1SCHEP31
        Object[] quadroEconomicoAndValmax = calcQuadroEconomicoConG1SCHEP31(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda,null, ngara);
        
        aggiudicazioneA3_1.setQuadroEconomicoStandard((QuadroEconomico) quadroEconomicoAndValmax[0]);

        aggiudicazioneA3_1.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if (dattoa != null) {
          aggiudicazioneA3_1.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }


        if(modlicg != null && !Long.valueOf(6).equals(modlicg)) {
          Double offertaMininmoRibasso = null;
          if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
            offertaMininmoRibasso = npaFunzioniManager.getOffertaMinimoRibasso(codGar, ngara);
          aggiudicazioneA3_1.setDatiBaseSottomissioniRicevute(new DatiBaseSottomissioniRicevute(offertaMininmoRibasso, npaFunzioniManager.getOffertaMassimoRibasso(codGar, ngara)));
        }
        
        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA3_1.setOffertePresentate(datiOfferte);
        }

        listaAggiudicazioneA3_1.add(aggiudicazioneA3_1);
      }
      anacFormA3_1.setAggiudicazioni(listaAggiudicazioneA3_1);
    }


    String json = ow.writeValueAsString(anacFormA3_1);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_A3_2(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA3_2 anacFormA3_2 = new AnacFormA3_2();

    AppaltoA3_2 appaltoA3_2 = new AppaltoA3_2();
    appaltoA3_2.setModalitaRiaggiudicazioneAffidamento(null);
    appaltoA3_2.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));

    anacFormA3_2.setAppalto(appaltoA3_2);


    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA3_2> listaAggiudicazioneA3_2 = new ArrayList<AggiudicazioneA3_2>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA3_2 aggiudicazioneA3_2 = new AggiudicazioneA3_2();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Double limmax = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 6).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 47).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();
        
        if(codcig != null) {
          aggiudicazioneA3_2.setCig(codcig);
        }

        if(limmax != null) {
          //recupero il valore assoluto
          Double limmaxAssoluto = Math.abs(limmax);
          if(String.valueOf(limmaxAssoluto).matches(pattern_numerico)) {
            aggiudicazioneA3_2.setValoreSogliaAnomalia(limmaxAssoluto);
          }
        } else {
          //Errore pattern
        }

        aggiudicazioneA3_2.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));

        aggiudicazioneA3_2.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if (dattoa != null) {
          aggiudicazioneA3_2.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }


        if(modlicg != null && !Long.valueOf(6).equals(modlicg)) {
          Double offertaMininmoRibasso = null;
          if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
            offertaMininmoRibasso = npaFunzioniManager.getOffertaMinimoRibasso(codGar, ngara);
          aggiudicazioneA3_2.setDatiBaseSottomissioniRicevute(new DatiBaseSottomissioniRicevute(offertaMininmoRibasso, npaFunzioniManager.getOffertaMassimoRibasso(codGar, ngara)));
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA3_2.setOffertePresentate(datiOfferte);
        }        
        
        listaAggiudicazioneA3_2.add(aggiudicazioneA3_2);
      }
      anacFormA3_2.setAggiudicazioni(listaAggiudicazioneA3_2);
    }


    String json = ow.writeValueAsString(anacFormA3_2);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_A3_3(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA3_3 anacFormA3_3 = new AnacFormA3_3();

    AppaltoA3_3 appaltoA3_3 = new AppaltoA3_3();

    String getCodcigaq = "select codcigaq "
        + "from torn "
        + "where codgar = ? ";
    String codcigaq = (String) this.sqlManager.getObject(getCodcigaq, new Object[]{codGar});

    appaltoA3_3.setCigAccordoQuadro(codcigaq);
    appaltoA3_3.setModalitaRiaggiudicazioneAffidamento(null);
    appaltoA3_3.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));

    anacFormA3_3.setAppalto(appaltoA3_3);


    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA3_3> listaAggiudicazioneA3_3 = new ArrayList<AggiudicazioneA3_3>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA3_3 aggiudicazioneA3_3 = new AggiudicazioneA3_3();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();

        if(codcig != null) {
          aggiudicazioneA3_3.setCig(codcig);
        }

        aggiudicazioneA3_3.setDataAdesione(dattoa);

        aggiudicazioneA3_3.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        aggiudicazioneA3_3.setQuadroEconomicoConcessioni(null);

        aggiudicazioneA3_3.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if (dattoa != null) {
          aggiudicazioneA3_3.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA3_3.setOffertePresentate(datiOfferte);
        }
        
        listaAggiudicazioneA3_3.add(aggiudicazioneA3_3);
      }
      anacFormA3_3.setAggiudicazioni(listaAggiudicazioneA3_3);
    }


    String json = ow.writeValueAsString(anacFormA3_3);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }
  
  private void registraJSON_A3_6(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA3_6 anacFormA3_6 = new AnacFormA3_6();

    anacFormA3_6.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    
    AppaltoA3_6 appaltoA3_6 = new AppaltoA3_6();

    String getDatiTorn = "select t.reluproc, g.not_gar, v.genere, t.destor, t.settore, t.accqua "
        + "from torn t "
        + "inner join gare g on g.codgar1 = t.codgar "
        + "inner join gare1 g1 on g1.codgar1 = g.codgar1 "
        + "inner join v_gare_genere v on v.codgar = g.codgar1 "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue());
      String not_gar = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      String accqua = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();

      String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
      appaltoA3_6.setCodiceAppalto(codiceAppaltoNpa);
      appaltoA3_6.setSettore(NpaFunzioniManager.getEnumSettore(settore));
      appaltoA3_6.setAccordoQuadro("1".equals(accqua));
      appaltoA3_6.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);
      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoA3_6.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(destor, null));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoA3_6.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(not_gar, null));          
        }
      }
      
    }
    anacFormA3_6.setAppalto(appaltoA3_6);

    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA3_6> listaAggiudicazioneA3_6 = new ArrayList<AggiudicazioneA3_6>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA3_6 aggiudicazioneA3_6 = new AggiudicazioneA3_6();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();

        aggiudicazioneA3_6.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        aggiudicazioneA3_6.setAfferenteInvestimentiPNRR(ispnrr);
        
        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        aggiudicazioneA3_6.setAcquisizioneCup(acquisizioneCup);
        aggiudicazioneA3_6.setCupLotto(risultato.get(acquisizioneCup));

        aggiudicazioneA3_6.setCcnl(codcnel);
        
        aggiudicazioneA3_6.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          aggiudicazioneA3_6.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }
        
        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            aggiudicazioneA3_6.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            aggiudicazioneA3_6.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }
        
        aggiudicazioneA3_6.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        if (npaFunzioniManager.isApplicazioneVersioneAttiva("3") && isconces) {
          aggiudicazioneA3_6.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));
        } else {
          aggiudicazioneA3_6.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge,
                  impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));
        }

        //gestione partecipanti
        aggiudicazioneA3_6.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD5DiscrAQ(ngara));

        aggiudicazioneA3_6.setDatiBase(new DatiBaseLotto(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, null));
        aggiudicazioneA3_6.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        aggiudicazioneA3_6.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneA3_6.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }
    
        if (dattoa != null) {
          aggiudicazioneA3_6.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        
        listaAggiudicazioneA3_6.add(aggiudicazioneA3_6);
      }
      anacFormA3_6.setAggiudicazioni(listaAggiudicazioneA3_6);
    }


    String json = ow.writeValueAsString(anacFormA3_6);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P3_1(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP3_1 anacFormP3_1 = new AnacFormP3_1();

    anacFormP3_1.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP3_1 appaltoP3_1 = new AppaltoP3_1();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP3_1.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.strumproc, t.idappaltonpa, "
        + "t.accqua, t.ricastae, t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.gartel, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 2);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 3);
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();

      
      appaltoP3_1.setTermineRidottoAvvisoPreinformazione(terrid);
      appaltoP3_1.setIdPianificazione(idpianinpa);
      appaltoP3_1.setSettore(NpaFunzioniManager.getEnumSettore(settore));

      if(strumproc != null) {
        appaltoP3_1.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP3_1.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      //recupero il valmax come somma dei valmax dei singoli lotti, tenendo conto che possono essere stati ridefiniti in G1SCHEP31
      Object valMaxObj = sqlManager.getObject("select SUM(CASE WHEN g1schep31.valmax is not null THEN g1schep31.valmax ELSE v_gare_importi.valmax END) "
              + "from v_gare_importi left join g1schep31 on v_gare_importi.ngara=g1schep31.ngara "
              +" where v_gare_importi.codgar=? and v_gare_importi.ngara is not null", new Object[] {codGar});
      Double valMax = null;
      if (valMaxObj instanceof Long) {
        valMax = ((Long) valMaxObj).doubleValue();
      } else {
        valMax = (Double) valMaxObj;
      }
      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP3_1.setDatiBase(new DatiBaseAppaltoOptional(destor, null, valMax));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP3_1.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, valMax));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP3_1.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }
    anacFormP3_1.setAppalto(appaltoP3_1);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP3_1> listalotti = new ArrayList<LottoP3_1>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP3_1 lotto = new LottoP3_1();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Long motesclu = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 31).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();


        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }
        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));
        
        //calcolo il quadro economico e il valmax tenendo conto della possibile presenza di un record in G1SCHEP31
        Object[] quadroEconomicoAndValmax = calcQuadroEconomicoConG1SCHEP31(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda,valmax, ngara);

        lotto.setQuadroEconomicoStandard((QuadroEconomico) quadroEconomicoAndValmax[0]);

        if(motesclu != null) {
          lotto.setMotivoEsclusioneOrdinarioSpeciale(new EnumNPA("motivoEsclusioneOrdinarioSpeciale", String.valueOf(motesclu)));
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, (Double) quadroEconomicoAndValmax[1]));

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        //ricalcolo i termini tenendo conto dell'eventuale record in G1SCHEP31. desoff,oesoff non previsti per la P3_1
        Vector terminiG1SCHEP31 = sqlManager.getVector("select dtepar,otepar,dteoff,oteoff from g1schep31 where ngara is null and codgar=?", new Object[] {codGar});
        if(terminiG1SCHEP31!=null && terminiG1SCHEP31.size()>0) {
          String g1schep31_dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(terminiG1SCHEP31, 0).getValue());
          String g1schep31_otepar =(String) SqlManager.getValueFromVectorParam(terminiG1SCHEP31, 1).getValue();
          String g1schep31_dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(terminiG1SCHEP31, 2).getValue());
          String g1schep31_oteoff =(String) SqlManager.getValueFromVectorParam(terminiG1SCHEP31, 3).getValue();
          if(g1schep31_dtepar!=null) dtepar=g1schep31_dtepar;
          if(g1schep31_otepar!=null) otepar=g1schep31_otepar;
          if(g1schep31_dteoff!=null) dteoff=g1schep31_dteoff;
          if(g1schep31_oteoff!=null) oteoff=g1schep31_oteoff;
        }
        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          lotto.setCcnl(codcnel);


        listalotti.add(lotto);
      }
      anacFormP3_1.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP3_1);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P3_2(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP3_2 anacFormP3_2 = new AnacFormP3_2();

    anacFormP3_2.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP3_2 appaltoP3_2 = new AppaltoP3_2();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP3_2.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.strumproc, t.idappaltonpa, "
        + "t.accqua, t.ricastae, t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.gartel, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 2);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 3);
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();

      
      appaltoP3_2.setTermineRidottoAvvisoPreinformazione(terrid);
      appaltoP3_2.setIdPianificazione(idpianinpa);
      appaltoP3_2.setSettore(NpaFunzioniManager.getEnumSettore(settore));

      if(strumproc != null) {
        appaltoP3_2.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP3_2.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP3_2.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP3_2.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP3_2.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }
    anacFormP3_2.setAppalto(appaltoP3_2);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP3_2> listalotti = new ArrayList<LottoP3_2>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP3_2 lotto = new LottoP3_2();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long motesclc = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 55).getValue();


        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        if(motesclc != null) {
          lotto.setMotivoEsclusioneConcessione(new EnumNPA("motivoEsclusioneConcessione", String.valueOf(motesclc)));
        }

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          lotto.setCcnl(codcnel);

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));



        listalotti.add(lotto);
      }
      anacFormP3_2.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP3_2);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P3_3(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP3_3 anacFormP3_3 = new AnacFormP3_3();

    anacFormP3_3.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP3_3 appaltoP3_3 = new AppaltoP3_3();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP3_3.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.strumproc, t.idappaltonpa, "
        + "t.ricastae, t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.codcigaq, t.gartel, t.settore, t.accqua, t.issda "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      JdbcParametro  ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 2);
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      String codcigaq = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 11);
      JdbcParametro issda = SqlManager.getValueFromVectorParam(vetDatiTorn, 12);

      appaltoP3_3.setIdPianificazione(idpianinpa);
      appaltoP3_3.setTermineRidottoAvvisoPreinformazione(terrid);
      appaltoP3_3.setSettore(NpaFunzioniManager.getEnumSettore(settore));

      appaltoP3_3.setCigAccordoQuadroConvenzione(codcigaq);

      if(strumproc != null) {
        appaltoP3_3.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP3_3.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP3_3.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP3_3.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      parameters.put("ISSDA",issda);
      appaltoP3_3.setDatiBaseStrumentiProcedura(new DatiBaseStrumentiProceduraCompleto(parameters));

    }
    anacFormP3_3.setAppalto(appaltoP3_3);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP3_3> listalotti = new ArrayList<LottoP3_3>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP3_3 lotto = new LottoP3_3();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Long motesclu = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 31).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();


        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        if(motesclu != null) {
          lotto.setMotivoEsclusioneOrdinarioSpeciale(new EnumNPA("motivoEsclusioneOrdinarioSpeciale", String.valueOf(motesclu)));
        }

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));
        if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          lotto.setCcnl(codcnel);

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));



        listalotti.add(lotto);
      }
      anacFormP3_3.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP3_3);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P6_2(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormP6_2 anacFormP6_2 = new AnacFormP6_2();
    anacFormP6_2.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP6_2 appaltoP6_2 = new AppaltoP6_2();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP6_2.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.strumproc, t.idappaltonpa, t.terrid, t.iterga, "
        + "v.genere, t.destor, t.idpianinpa, t.prourg, t.motacc, t.accqua, t.ricastae, t.codcigaq, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      Boolean prourg = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue());
      String motacc = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 9);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 10);
      String codcigaq = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 12).getValue();

      appaltoP6_2.setCigAccordoQuadro(codcigaq);
      
      if(strumproc != null) {
        appaltoP6_2.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
      appaltoP6_2.setIdPianificazione(idpianinpa);

      appaltoP6_2.setTermineRidottoAvvisoPreinformazione(terrid);

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP6_2.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP6_2.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      DatiBaseProceduraCompleto baseProcedura = new DatiBaseProceduraCompleto();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));
      if(prourg != null && Boolean.TRUE.equals(prourg)) {
        baseProcedura.setProceduraAccelerata(Boolean.TRUE);
      } else {
        baseProcedura.setProceduraAccelerata(Boolean.FALSE);
      }
      if(motacc != null) {
        baseProcedura.setGiustificazioneProceduraAccelerata(motacc);
      }
      appaltoP6_2.setDatiBaseProcedura(baseProcedura);

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProceduraOptional baseStrumentiProcedura = new DatiBaseStrumentiProceduraOptional(parameters);
      appaltoP6_2.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }
    anacFormP6_2.setAppalto(appaltoP6_2);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP6_2> listalotti = new ArrayList<LottoP6_2>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP6_2 lotto = new LottoP6_2();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        lotto.setStrumentiElettroniciSpecifici(isstrumel);
        
        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        lotto.setCui(codcui);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }


        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvioSoloOra(npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        listalotti.add(lotto);
      }
      anacFormP6_2.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP6_2);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);


  }

  private void registraJSON_S3(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormS3 anacFormS3 = new AnacFormS3();


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<ElencoIncarichi> listaElencoIncarichi = new ArrayList<ElencoIncarichi>();
      for (int i = 0; i < datiLotti.size(); i++) {
        ElencoIncarichi elencoIncarichi = new ElencoIncarichi();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();

        elencoIncarichi.setCig(codcig);

        elencoIncarichi.setPrestazioni(null);

        String getDati = "select c.cftec, c.cogtei, c.nometei, c.teltec, "
            + "c.faxtec, c.ematec, c.indtec, c.ncitec, c.captec, c.cittec, c.loctec "
            + "from torn t "
            + "inner join UFFINT u on t.cenint = u.codein "
            + "inner join tecni c on u.codres = c.codtec "
            + "where t.codgar = ? ";

        List<String> datiTecni = this.sqlManager.getListVector(getDati, new Object[]{codGar});
        if(datiTecni!=null && !datiTecni.isEmpty()){
          List<Incarico> listaIncarichi = new ArrayList<Incarico>();
          for (int y = 0; y < datiTecni.size(); y++) {
            Incarico incarico = new Incarico();
            DatiPersonaFisica datiPersonaFisica = new DatiPersonaFisica();
            String cftec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 0).getValue();
            String cogteci = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 1).getValue();
            String nometei = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 2).getValue();
            String teltec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 3).getValue();
            String faxtec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 4).getValue();
            String ematec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 5).getValue();
            String indtec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 6).getValue();
            String ncitec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 7).getValue();
            String captec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 8).getValue();
            String cittec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 9).getValue();
            String loctec = (String) SqlManager.getValueFromVectorParam(datiTecni.get(y), 10).getValue();


            datiPersonaFisica.setCodiceFiscale(cftec);
            datiPersonaFisica.setCognome(cogteci);
            datiPersonaFisica.setNome(nometei);
            datiPersonaFisica.setTelefono(teltec);
            datiPersonaFisica.setFax(faxtec);
            datiPersonaFisica.setEmail(ematec);
            if(indtec != null) {
              String indirizzo = indtec;
              if(ncitec != null)
                indirizzo += " "+ncitec;
              if(loctec != null)
                indirizzo += " "+loctec;
              datiPersonaFisica.setIndirizzo(indirizzo);
            }
            datiPersonaFisica.setCap(captec);
            datiPersonaFisica.setCodIstat(new EnumNPA("codIstat", cittec));
            datiPersonaFisica.setIncaricatoEstero(Boolean.valueOf(false));
            incarico.setDatiPersonaFisica(datiPersonaFisica);
            incarico.setDatiPersonaGiuridica(null);
            listaIncarichi.add(incarico);
          }
          elencoIncarichi.setIncarichi(listaIncarichi);
        }
        listaElencoIncarichi.add(elencoIncarichi);
      }
      anacFormS3.setElencoIncarichi(listaElencoIncarichi);
    }

    String json = ow.writeValueAsString(anacFormS3);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);
    //String uuid = UUID.randomUUID().toString();
    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P7_1_2(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP7_1_2 anacFormP7_1_2 = new AnacFormP7_1_2();
    anacFormP7_1_2.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP7Base appaltoP7Base = new AppaltoP7Base();
    appaltoP7Base.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP7Base.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.moturg, t.sommaur, t.strumproc, t.motcig, t.tipgar, t.idappaltonpa, "
        + "t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.accqua, t.ricastae, t.gartel, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 11);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 12);
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 13).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 14).getValue();

      appaltoP7Base.setIdPianificazione(idpianinpa);

      appaltoP7Base.setContrattoDifesa(false);
      appaltoP7Base.setSettore(NpaFunzioniManager.getEnumSettore(settore));

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP7Base.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP7Base.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP7Base.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP7Base.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }
      if(strumproc != null) {
        appaltoP7Base.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP7Base.setTermineRidottoAvvisoPreinformazione(terrid);

      appaltoP7Base.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP7Base.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP7Base.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP7Base.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }
    anacFormP7_1_2.setAppalto(appaltoP7Base);





    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP7_1_2> listalotti = new ArrayList<LottoP7_1_2>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP7_1_2 lotto = new LottoP7_1_2();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Long motesclu = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 31).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long motesclc = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 55).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }
        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }
        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(oggcont != null) {
          if(Long.valueOf(2).equals(oggcont) || Long.valueOf(3).equals(oggcont)) {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "2"));
          } else {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "1"));
          }
        }

        lotto.setServizioPubblicoLocale(serviloc);

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setCcnl(codcnel);

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));



        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvioSoloScadenza(npaFunzioniManager.unisciDataeOra(dtepar, otepar)));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        if(motesclu != null) {
          lotto.setMotivoEsclusioneOrdinarioSpeciale(new EnumNPA("motivoEsclusioneOrdinarioSpeciale", String.valueOf(motesclu)));
        }

        if(motesclc != null) {
          lotto.setMotivoEsclusioneConcessione(new EnumNPA("motivoEsclusioneConcessione", String.valueOf(motesclc)));
        }

        lotto.setTipoRealizzazione(new EnumNPA("tipoRealizzazione", "1"));

        String getDateTorn = "select DINVIT from TORN where codgar = ? ";
        Vector<?> vetDateTorn = this.sqlManager.getVector(getDateTorn, new Object[]{codGar});
        if(vetDateTorn!=null && !vetDateTorn.isEmpty()){
          String dataInvito = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(vetDateTorn, 0).getValue());

          lotto.setDatiBaseComunicazione(new DatiBaseComunicazione(dataInvito));
        }

        listalotti.add(lotto);
      }
      anacFormP7_1_2.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP7_1_2);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P7_2(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP7_2 anacFormP7_2 = new AnacFormP7_2();
    anacFormP7_2.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP7Base appaltoP7Base = new AppaltoP7Base();
    appaltoP7Base.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP7Base.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.moturg, t.sommaur, t.strumproc, t.motcig, t.tipgar, t.idappaltonpa, "
        + "t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.accqua, t.ricastae, t.gartel, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 11);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 12);
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 13).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 14).getValue();


      appaltoP7Base.setTermineRidottoAvvisoPreinformazione(terrid);
      appaltoP7Base.setIdPianificazione(idpianinpa);

      appaltoP7Base.setContrattoDifesa(false);
      appaltoP7Base.setSettore(NpaFunzioniManager.getEnumSettore(settore));
      

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP7Base.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP7Base.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP7Base.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP7Base.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }
      if(strumproc != null && !npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
        appaltoP7Base.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP7Base.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP7Base.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP7Base.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }
      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP7Base.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }
    anacFormP7_2.setAppalto(appaltoP7Base);





    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP7_2> listalotti = new ArrayList<LottoP7_2>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP7_2 lotto = new LottoP7_2();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Long motesclu = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 31).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long motesclc = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 55).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));


        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }
        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }
        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(oggcont != null) {
          if(Long.valueOf(2).equals(oggcont) || Long.valueOf(3).equals(oggcont)) {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "2"));
          } else {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "1"));
          }
        }

        lotto.setServizioPubblicoLocale(serviloc);

        if(motesclc != null) {
          lotto.setMotivoEsclusioneConcessione(new EnumNPA("motivoEsclusioneConcessione", String.valueOf(motesclc)));
        }

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        lotto.setCui(codcui);
        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setCcnl(codcnel);

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvioSoloOra(npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        if(motesclu != null) {
          lotto.setMotivoEsclusioneOrdinarioSpeciale(new EnumNPA("motivoEsclusioneOrdinarioSpeciale", String.valueOf(motesclu)));
        }

        lotto.setTipoRealizzazione(new EnumNPA("tipoRealizzazione", "1"));

        String getDateTorn = "select DINVIT from TORN where codgar = ? ";
        Vector<?> vetDateTorn = this.sqlManager.getVector(getDateTorn, new Object[]{codGar});
        if(vetDateTorn!=null && !vetDateTorn.isEmpty()){
          String dataInvito = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(vetDateTorn, 0).getValue());

          lotto.setDatiBaseComunicazione(new DatiBaseComunicazione(dataInvito));
        }

        listalotti.add(lotto);
      }
      anacFormP7_2.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP7_2);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);    

  }

  private void registraJSON_PL2(Long idScheda, String codgar, String nomeScheda) throws Exception {
    AnacFormPL2 anacForm = npaFunzioniManager.getAnacFormPL2(idScheda, codgar, nomeScheda);
    String json = ow.writeValueAsString(anacForm);
    npaFunzioniManager.insertJsonNpa(null, codgar, null, nomeScheda, json, null);
  }

  private void registraJSON_P7_3(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormP7_3 anacFormP7_3 = new AnacFormP7_3();
    anacFormP7_3.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP7_3 appaltoP7_3 = new AppaltoP7_3();

    String getDatiTorn = "select t.idappaltonpa, gb.oggetto, "
        + "gb.tipoele, gb.dinizval, gb.dtermval, gb.ngara, t.settore "
        + "from torn t "
        + "inner join garealbo gb on t.codgar = gb.codgar "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      String oggetto = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      String tipoele = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      String dinizval = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      String dtermval = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue());
      String ngara = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();

      appaltoP7_3.setContrattoDifesa(false);
      appaltoP7_3.setSettore(NpaFunzioniManager.getEnumSettore(settore));
      appaltoP7_3.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(oggetto, null));
      anacFormP7_3.setAppalto(appaltoP7_3);

      LottoP7_3 lottoP7_3 = new LottoP7_3();
      lottoP7_3.setLotIdentifier("LOT-0001");
      lottoP7_3.setDatiBase(new DatiBaseLotto(npaFunzioniManager.getOggettoContrattoByTipoEle(tipoele), oggetto, null));
      lottoP7_3.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

      DatiBaseDurata datiBaseDurata = new DatiBaseDurata();
      datiBaseDurata.setDataInizio(dinizval);
      datiBaseDurata.setDataFine(dtermval);
      lottoP7_3.setDatiBaseDurata(datiBaseDurata);

      lottoP7_3.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

      List<LottoP7_3> listaLotti = new ArrayList<LottoP7_3>();
      listaLotti.add(lottoP7_3);
      anacFormP7_3.setLotti(listaLotti);
    }
    String json = ow.writeValueAsString(anacFormP7_3);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);
  }

  private void registraJSON_P2_23(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_23 anacFormP2_23 = new AnacFormP2_23();
    List<String> listaCodiciLotto = new ArrayList<String>();
    anacFormP2_23.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_23 appaltoP2_23 = new AppaltoP2_23();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP2_23.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;
    Long genere = null;

            String getDatiTorn = "select t.strumproc, t.idappaltonpa, "
        + "t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();

      Long dispopart = npaFunzioniManager.getDispopartByCodgarSql(codGar);

      
      appaltoP2_23.setTermineRidottoAvvisoPreinformazione(terrid);
      if(dispopart != null && !npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
        appaltoP2_23.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
      }
      if(strumproc != null) {
        appaltoP2_23.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP2_23.setIdPianificazione(idpianinpa);

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_23.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_23.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));   
        }
      }
      appaltoP2_23.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

    }

    anacFormP2_23.setAppalto(appaltoP2_23);

    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_23> listalotti = new ArrayList<LottoP2_23>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_23 lotto = new LottoP2_23();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long motesclu = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 31).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impproge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 32).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Long dispopartLotto = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        listaCodiciLotto.add(ngara);

        lotto.setStrumentiElettroniciSpecifici(isstrumel);
        
        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        if(motesclu != null) {
          lotto.setMotivoEsclusioneOrdinarioSpeciale(new EnumNPA("motivoEsclusioneOrdinarioSpeciale", String.valueOf(motesclu)));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        lotto.setQuadroEconomicoConcorsiProgettazione(new QuadroEconomicoConcorsiProgettazione(impapp, impproge));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        lotto.setDatiBaseImporto(new DatiBaseImporto(Boolean.valueOf(false)));

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopartLotto)));

        listalotti.add(lotto);
      }
      anacFormP2_23.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_23);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);
  }


  private void registraJSON_P2_24(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_24 anacFormP2_24 = new AnacFormP2_24();
    List<String> listaCodiciLotto = new ArrayList<String>();
    anacFormP2_24.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_24 appaltoP2_24 = new AppaltoP2_24();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP2_24.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;
    Long genere = null;

    String getDatiTorn = "select t.strumproc, t.idappaltonpa, "
        + "t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();

      
      appaltoP2_24.setTermineRidottoAvvisoPreinformazione(terrid);

      if(strumproc != null) {
        appaltoP2_24.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP2_24.setIdPianificazione(idpianinpa);

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_24.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_24.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));   
        }
      }
      appaltoP2_24.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

    }

    anacFormP2_24.setAppalto(appaltoP2_24);

    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_24> listalotti = new ArrayList<LottoP2_24>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_24 lotto = new LottoP2_24();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long motesclu = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 31).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impproge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 32).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        listaCodiciLotto.add(ngara);
        
        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        if(motesclu != null) {
          lotto.setMotivoEsclusioneOrdinarioSpeciale(new EnumNPA("motivoEsclusioneOrdinarioSpeciale", String.valueOf(motesclu)));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        lotto.setQuadroEconomicoConcorsiProgettazione(new QuadroEconomicoConcorsiProgettazione(impapp, impproge));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        lotto.setDatiBaseImporto(new DatiBaseImporto(Boolean.valueOf(false)));

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        listalotti.add(lotto);
      }
      anacFormP2_24.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_24);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);
  }

  private void registraJSON_AD5(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    AnacFormAD5 anacFormAD5 = new AnacFormAD5();

    anacFormAD5.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoAD5 appaltoAD5 = new AppaltoAD5();


    String getDatiTorn = "select t.idappaltonpa, v.genere, t.destor, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();


      appaltoAD5.setCodiceAppalto(codiceAppaltoNpa);
      appaltoAD5.setSettore(NpaFunzioniManager.getEnumSettore(settore));
      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoAD5.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(destor, null));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoAD5.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(npaFunzioniManager.getNotGarByCodgarSql(codGar), null));          
        }
      }
    }
    anacFormAD5.setAppalto(appaltoAD5);




    //array aggiudicazioni
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneAD5> listaAggiudicazioni = new ArrayList<AggiudicazioneAD5>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneAD5 aggiudicazioneAD5;

        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();

        if (npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          aggiudicazioneAD5 = new AggiudicazioneAD5<List<String>>();

          Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
          boolean acquisizioneCup = risultato.keySet().iterator().next();
          aggiudicazioneAD5.setCup(risultato.get(acquisizioneCup));
        } else {
          aggiudicazioneAD5 = new AggiudicazioneAD5<String>();

          aggiudicazioneAD5.setCup(cupprg);
        }

        aggiudicazioneAD5.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          aggiudicazioneAD5.setCcnl(codcnel);

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          aggiudicazioneAD5.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
          if(Long.valueOf(1).equals(tipgen)) {
            aggiudicazioneAD5.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
          }
        }
        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            aggiudicazioneAD5.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            aggiudicazioneAD5.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }
        aggiudicazioneAD5.setDatiBase(new DatiBaseLotto(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, null));
        aggiudicazioneAD5.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        aggiudicazioneAD5.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        //gestione partecipanti
        aggiudicazioneAD5.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD5DiscrAQ(ngara));

        aggiudicazioneAD5.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "A"));

        listaAggiudicazioni.add(aggiudicazioneAD5);  
      }
      anacFormAD5.setAggiudicazioni(listaAggiudicazioni);
    }

    String json = ow.writeValueAsString(anacFormAD5);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);


  }

  private void registraJSON_AD4(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    AnacFormAD4 anacFormAD4 = new AnacFormAD4();

    anacFormAD4.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoAD4 appaltoAD4 = new AppaltoAD4();

    String getDatiTorn = "select t.reluproc, t.urbasco, t.idappaltonpa, t.codcigaq, g.dattoa, "
        + "g.not_gar, v.genere, t.destor "
        + "from torn t "
        + "inner join gare g on g.codgar1 = t.codgar "
        + "inner join gare1 g1 on g1.codgar1 = g.codgar1 "
        + "inner join v_gare_genere v on v.codgar = g.codgar1 "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      String codcigaq = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue());
      String not_gar = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();

      

      appaltoAD4.setCodiceAppalto(codiceAppaltoNpa);
      appaltoAD4.setCig(codcigaq);
      appaltoAD4.setDataAdesione(dattoa);
      appaltoAD4.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);
      appaltoAD4.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);
      appaltoAD4.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar)))); 
      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoAD4.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(destor, null));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoAD4.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(not_gar, null));          
        }
      }
    }
    anacFormAD4.setAppalto(appaltoAD4);

    //array aggiudicazioni
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneAD4> listaAggiudicazioni = new ArrayList<AggiudicazioneAD4>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneAD4 aggiudicazioneAD4 = new AggiudicazioneAD4();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String accqua = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 29).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        aggiudicazioneAD4.setStrumentiElettroniciSpecifici(isstrumel);
        aggiudicazioneAD4.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        aggiudicazioneAD4.setCcnl(codcnel);
        aggiudicazioneAD4.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          aggiudicazioneAD4.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }
        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            aggiudicazioneAD4.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            aggiudicazioneAD4.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }
        aggiudicazioneAD4.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        aggiudicazioneAD4.setAcquisizioneCup(acquisizioneCup);
        aggiudicazioneAD4.setCupLotto(risultato.get(acquisizioneCup));

        aggiudicazioneAD4.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        if(Boolean.FALSE.equals(isconces)) {
        aggiudicazioneAD4.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));
        } else {
          aggiudicazioneAD4.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, 
              impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));
        }

        aggiudicazioneAD4.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        //gestione partecipanti
        aggiudicazioneAD4.setPartecipanti(npaFunzioniManager.getListaPartecipADDiscrAQ(ngara));

        aggiudicazioneAD4.setDatiBase(new DatiBaseLotto(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, null));

        aggiudicazioneAD4.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        aggiudicazioneAD4.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        if (dattoa != null) {
          aggiudicazioneAD4.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneAD4.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        aggiudicazioneAD4.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "A"));

        listaAggiudicazioni.add(aggiudicazioneAD4);  
      }
      anacFormAD4.setAggiudicazioni(listaAggiudicazioni);

    }


    String json = ow.writeValueAsString(anacFormAD4);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);


  }

  private void registraJSON_AD3(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    AnacFormAD3 anacFormAD3 = new AnacFormAD3();

    anacFormAD3.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoAD3 appaltoAD3 = new AppaltoAD3();

    String getDatiTorn = "select t.moturg, t.sommaur, t.reluproc, t.urbasco, "
        + "t.idappaltonpa, g.not_gar, v.genere, t.destor, t.settore, t.accqua "
        + "from torn t "
        + "inner join gare g on g.codgar1 = t.codgar "
        + "inner join v_gare_genere v on v.codgar = g.codgar1 "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      String not_gar = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      String accqua = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();

      
      appaltoAD3.setCodiceAppalto(codiceAppaltoNpa);

      appaltoAD3.setContrattoDifesa(false);
      appaltoAD3.setSettore(NpaFunzioniManager.getEnumSettore(settore));
      appaltoAD3.setAccordoQuadro("1".equals(accqua));

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoAD3.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoAD3.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoAD3.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoAD3.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoAD3.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoAD3.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(destor, null));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoAD3.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(not_gar, null));          
        }
      }

      appaltoAD3.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));


    }
    anacFormAD3.setAppalto(appaltoAD3);



    //array aggiudicazioni
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneAD3> listaAggiudicazioni = new ArrayList<AggiudicazioneAD3>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneAD3 aggiudicazioneAD3 = new AggiudicazioneAD3();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        aggiudicazioneAD3.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        aggiudicazioneAD3.setCcnl(codcnel);
        aggiudicazioneAD3.setStrumentiElettroniciSpecifici(isstrumel);
        aggiudicazioneAD3.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          aggiudicazioneAD3.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            aggiudicazioneAD3.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            aggiudicazioneAD3.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        aggiudicazioneAD3.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        aggiudicazioneAD3.setAcquisizioneCup(acquisizioneCup);
        aggiudicazioneAD3.setCupLotto(risultato.get(acquisizioneCup));

        aggiudicazioneAD3.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        if(Boolean.FALSE.equals(isconces)) {
          aggiudicazioneAD3.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge,
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));
        } else {
          aggiudicazioneAD3.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp,
                  impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));
        }

        //gestione partecipanti
        aggiudicazioneAD3.setPartecipanti(npaFunzioniManager.getListaPartecipADDiscrAQ(ngara));
        //gestione partecipanti fine

        aggiudicazioneAD3.setDatiBase(new DatiBaseLotto(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, null));

        aggiudicazioneAD3.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        aggiudicazioneAD3.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        if (dattoa != null) {
          aggiudicazioneAD3.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneAD3.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        aggiudicazioneAD3.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "A"));

        listaAggiudicazioni.add(aggiudicazioneAD3);  
      }
      anacFormAD3.setAggiudicazioni(listaAggiudicazioni);
    }


    String json = ow.writeValueAsString(anacFormAD3);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }
  
  private void registraJSON_A7_1_1(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA7_1_1 anacFormA7_1_1 = new AnacFormA7_1_1();
    AppaltoA7_1_1 appaltoA7_1_1 = new AppaltoA7_1_1();

    Long tipgen = null;

    String getDatiTorn = "select moturg, sommaur, reluproc, urbasco, tipgen "
        + "from torn "
        + "where codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      tipgen = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      
      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoA7_1_1.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoA7_1_1.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoA7_1_1.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoA7_1_1.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoA7_1_1.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

    }
    anacFormA7_1_1.setAppalto(appaltoA7_1_1);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA7_1_1> listaAggiudicazioni = new ArrayList<AggiudicazioneA7_1_1>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA7_1_1 aggiudicazioneA7_1_1 = new AggiudicazioneA7_1_1();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Double limmax = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 6).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();


        aggiudicazioneA7_1_1.setCig(codcig);

        if(limmax != null) {
          //recupero il valore assoluto
          Double limmaxAssoluto = Math.abs(limmax);
          if(String.valueOf(limmaxAssoluto).matches(pattern_numerico)) {
            aggiudicazioneA7_1_1.setValoreSogliaAnomalia(limmaxAssoluto);
          }
        } else {
          //Errore pattern
        }

        if(Boolean.FALSE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("4"))) {
          aggiudicazioneA7_1_1.setAfferenteInvestimentiPNRR(ispnrr);

          Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
          boolean acquisizioneCup = risultato.keySet().iterator().next();
          aggiudicazioneA7_1_1.setAcquisizioneCup(acquisizioneCup);
          aggiudicazioneA7_1_1.setCupLotto(risultato.get(acquisizioneCup));

          aggiudicazioneA7_1_1.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova,
                  npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));
        }

        if(Boolean.FALSE.equals(isconces)) {
          aggiudicazioneA7_1_1.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge,
              impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));
        } else {
          aggiudicazioneA7_1_1.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp,
                  impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));
        }

        if(Boolean.FALSE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("4"))) {
          //gestione partecipanti
          String getDittaPartecipanti = null;
          List<String> datiDitta = null;
          getDittaPartecipanti = "select dq.dittao, dq.iaggiu, d.idpartecipnpa "
                  + "from ditgaq dq inner join ditg d "
                  + "on d.ngara5 = dq.ngara and d.dittao = dq.dittao "
                  + "where dq.ngara=?";
          datiDitta = this.sqlManager.getListVector(getDittaPartecipanti, new Object[]{ngara});
          if (datiDitta != null && !datiDitta.isEmpty()) {
            if (Long.valueOf(3).equals(npaFunzioniManager.getGenereByCodgar(codGar))) {
              aggiudicazioneA7_1_1.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD(datiDitta, codGar, false, true));
            } else {
              aggiudicazioneA7_1_1.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD(datiDitta, ngara, false, true));
            }
          } else {
            getDittaPartecipanti = "select g.ditta, g.iaggiu, d.idpartecipnpa "
                    + "from gare g inner join ditg d "
                    + "on d.codgar5 = g.codgar1 and d.ngara5 = g.ngara and d.dittao = g.ditta "
                    + "where g.ngara = ?";
            datiDitta = this.sqlManager.getListVector(getDittaPartecipanti, new Object[]{ngara});
            if (datiDitta != null && !datiDitta.isEmpty()) {
              if (Long.valueOf(3).equals(npaFunzioniManager.getGenereByCodgar(codGar))) {
                aggiudicazioneA7_1_1.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD(datiDitta, codGar, false, true));
              } else {
                aggiudicazioneA7_1_1.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD(datiDitta, ngara, false, true));
              }
            }
          }
          //gestione partecipanti fine

          aggiudicazioneA7_1_1.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          if(Long.valueOf(4).equals(esineg)) {
            esineg = Long.valueOf(6);
          } else {
            esineg = Long.valueOf(7);
          }
          aggiudicazioneA7_1_1.setEsitoProceduraAnnullata(new EnumNPA("esitoProceduraAnnullata", String.valueOf(esineg)));
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA7_1_1.setOffertePresentate(datiOfferte);
        }
        
        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA7_1_1.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA7_1_1.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA7_1_1.setNumeroOfferteAmmesse(nofval);
        }

        listaAggiudicazioni.add(aggiudicazioneA7_1_1);
      }
      anacFormA7_1_1.setAggiudicazioni(listaAggiudicazioni);
    }

    String json = ow.writeValueAsString(anacFormA7_1_1);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_A7_1_2(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA7_1_2 anacFormA7_1_2 = new AnacFormA7_1_2();
    AppaltoA7_1_2 appaltoA7_1_2 = new AppaltoA7_1_2();

    Long tipgen = null;

    String getDatiTorn = "select moturg, sommaur, reluproc, urbasco, tipgen, "
        + "prourg, motacc, iterga, accqua, motaggidir "
        + "from torn "
        + "where codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      tipgen = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      Boolean prourg = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue());
      String motacc = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 8);
      String motaggidir = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoA7_1_2.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoA7_1_2.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoA7_1_2.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoA7_1_2.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoA7_1_2.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

      DatiBaseProcedura baseProcedura = new DatiBaseProcedura();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));

      List<EnumNPA> giustificazioniAggiudicazioneDiretta = new ArrayList<>();
      if(motaggidir != null) {
        giustificazioniAggiudicazioneDiretta.add(new EnumNPA("giustificazioniAggiudicazioneDiretta", npaFunzioniManager.getTab2d1ByTab2tip(motaggidir, "A1z22")));
      }
      baseProcedura.setGiustificazioniAggiudicazioneDiretta(giustificazioniAggiudicazioneDiretta);

      appaltoA7_1_2.setDatiBaseProcedura(baseProcedura);

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico baseStrumentiProcedura = new DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico(parameters);
      appaltoA7_1_2.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
      appaltoA7_1_2.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));
    }
    anacFormA7_1_2.setAppalto(appaltoA7_1_2);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA7_1_2> listaAggiudicazioni = new ArrayList<AggiudicazioneA7_1_2>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA7_1_2 aggiudicazioneA7_1_2 = new AggiudicazioneA7_1_2();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Double limmax = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 6).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 47).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();
        
        aggiudicazioneA7_1_2.setCig(codcig);

        if(limmax != null) {
          //recupero il valore assoluto
          Double limmaxAssoluto = Math.abs(limmax);
          if(String.valueOf(limmaxAssoluto).matches(pattern_numerico)) {
            aggiudicazioneA7_1_2.setValoreSogliaAnomalia(limmaxAssoluto);
          }
        } else {
          //Errore pattern
        }

        if(Boolean.FALSE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("4"))) {
          aggiudicazioneA7_1_2.setAfferenteInvestimentiPNRR(ispnrr);

          Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
          boolean acquisizioneCup = risultato.keySet().iterator().next();
          aggiudicazioneA7_1_2.setAcquisizioneCup(acquisizioneCup);
          aggiudicazioneA7_1_2.setCupLotto(risultato.get(acquisizioneCup));

          aggiudicazioneA7_1_2.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova,
                  npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));
        }

        if(Boolean.FALSE.equals(isconces)) {
          aggiudicazioneA7_1_2.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge,
              impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));
        } else {
          aggiudicazioneA7_1_2.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp,
                  impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));
        }

        if(Boolean.FALSE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("4"))) {
          //gestione partecipanti
          String getDittaPartecipanti = null;
          List<String> datiDitta = null;
          getDittaPartecipanti = "select dq.dittao, dq.iaggiu, d.idpartecipnpa "
                  + "from ditgaq dq inner join ditg d "
                  + "on d.ngara5 = dq.ngara and d.dittao = dq.dittao "
                  + "where dq.ngara=?";
          datiDitta = this.sqlManager.getListVector(getDittaPartecipanti, new Object[]{ngara});
          if (datiDitta != null && !datiDitta.isEmpty()) {
            if (Long.valueOf(3).equals(npaFunzioniManager.getGenereByCodgar(codGar))) {
              aggiudicazioneA7_1_2.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD(datiDitta, codGar, false, true));
            } else {
              aggiudicazioneA7_1_2.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD(datiDitta, ngara, false, true));
            }
          } else {
            getDittaPartecipanti = "select g.ditta, g.iaggiu, d.idpartecipnpa "
                    + "from gare g inner join ditg d "
                    + "on d.codgar5 = g.codgar1 and d.ngara5 = g.ngara and d.dittao = g.ditta "
                    + "where g.ngara = ?";
            datiDitta = this.sqlManager.getListVector(getDittaPartecipanti, new Object[]{ngara});
            if (datiDitta != null && !datiDitta.isEmpty()) {
              if (Long.valueOf(3).equals(npaFunzioniManager.getGenereByCodgar(codGar))) {
                aggiudicazioneA7_1_2.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD(datiDitta, codGar, false, true));
              } else {
                aggiudicazioneA7_1_2.setPartecipanti(npaFunzioniManager.getListaPartecipantiAD(datiDitta, ngara, false, true));
              }
            }
          }
          //gestione partecipanti fine

          aggiudicazioneA7_1_2.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));
        }

        if (dattoa != null) {
          aggiudicazioneA7_1_2.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(Boolean.FALSE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("4"))) {
          if (accessi != null) {
            EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
            aggiudicazioneA7_1_2.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
          }
        }
        
        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA7_1_2.setOffertePresentate(datiOfferte);
        }

        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA7_1_2.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA7_1_2.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA7_1_2.setNumeroOfferteAmmesse(nofval);
        }

        aggiudicazioneA7_1_2.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if(modlicg != null && !Long.valueOf(6).equals(modlicg)) {
          Double offertaMininmoRibasso = null;
          if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
            offertaMininmoRibasso = npaFunzioniManager.getOffertaMinimoRibasso(codGar, ngara);
          aggiudicazioneA7_1_2.setDatiBaseSottomissioniRicevute(new DatiBaseSottomissioniRicevute(offertaMininmoRibasso, npaFunzioniManager.getOffertaMassimoRibasso(codGar, ngara)));
        }



        listaAggiudicazioni.add(aggiudicazioneA7_1_2);
      }
      anacFormA7_1_2.setAggiudicazioni(listaAggiudicazioni);
    }

    String json = ow.writeValueAsString(anacFormA7_1_2);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_A2_36(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA2_36 anacFormA2_36 = new AnacFormA2_36();

    String getDatiTorn = "select iterga "
        + "from torn "
        + "where codgar = ? ";
    Long iterga = (Long) this.sqlManager.getObject(getDatiTorn, new Object[]{codGar});
    anacFormA2_36.setAppalto(new AppaltoA2_36(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar))));


    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA2_36> listaAggiudicazioneA2_36 = new ArrayList<AggiudicazioneA2_36>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA2_36 aggiudicazioneA2_36 = new AggiudicazioneA2_36();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();
        
        if(codcig != null) {
          aggiudicazioneA2_36.setCig(codcig);
        }

        aggiudicazioneA2_36.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if (dattoa != null) {
          aggiudicazioneA2_36.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneA2_36.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }
        
        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA2_36.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA2_36.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA2_36.setNumeroOfferteAmmesse(nofval);
        }
        
        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA2_36.setOffertePresentate(datiOfferte);
        }
        listaAggiudicazioneA2_36.add(aggiudicazioneA2_36);
      }
      anacFormA2_36.setAggiudicazioni(listaAggiudicazioneA2_36);
    }


    String json = ow.writeValueAsString(anacFormA2_36);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_A2_29(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA2_29 anacFormA2_29 = new AnacFormA2_29();
    AppaltoA2_29 appaltoA2_29 = new AppaltoA2_29();
    List<String> listaCodiciLotto = new ArrayList<String>();
    Long tipgen = null;

    String getDatiTorn = "select moturg, sommaur, reluproc, urbasco, tipgen, "
        + "prourg, motacc, iterga, accqua, ricastae, motaggidir, issda "
        + "from torn "
        + "where codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      tipgen = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      Boolean prourg = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue());
      String motacc = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 8);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 9);
      String motaggidir = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      JdbcParametro issda = SqlManager.getValueFromVectorParam(vetDatiTorn, 11);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoA2_29.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoA2_29.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoA2_29.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoA2_29.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoA2_29.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

      appaltoA2_29.setModalitaRiaggiudicazioneAffidamento(null);

      appaltoA2_29.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));

      DatiBaseProceduraACompleto baseProcedura = new DatiBaseProceduraACompleto();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));
      if(prourg != null && Boolean.TRUE.equals(prourg)) {
        baseProcedura.setProceduraAccelerata(Boolean.TRUE);
      } else {
        baseProcedura.setProceduraAccelerata(Boolean.FALSE);
      }

      List<EnumNPA> giustificazioniAggiudicazioneDiretta = new ArrayList<EnumNPA>();
      if(motaggidir != null) {
        giustificazioniAggiudicazioneDiretta.add(new EnumNPA("giustificazioniAggiudicazioneDiretta", npaFunzioniManager.getTab2d1ByTab2tip(motaggidir, "A1z22")));
      }
      baseProcedura.setGiustificazioniAggiudicazioneDiretta(giustificazioniAggiudicazioneDiretta);

      appaltoA2_29.setDatiBaseProcedura(baseProcedura);

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      parameters.put("ISSDA",issda);
      appaltoA2_29.setDatiBaseStrumentiProcedura(new DatiBaseStrumentiProceduraCompleto(parameters));

    }
    anacFormA2_29.setAppalto(appaltoA2_29);



    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA2_29> listaAggiudicazioneA2_29 = new ArrayList<AggiudicazioneA2_29>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA2_29 aggiudicazioneA2_29 = new AggiudicazioneA2_29();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Double limmax = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 6).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 47).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();
        
        listaCodiciLotto.add(ngara);

        if(codcig != null) {
          aggiudicazioneA2_29.setCig(codcig);
        }

        if(limmax != null) {
          //recupero il valore assoluto
          Double limmaxAssoluto = Math.abs(limmax);
          if(String.valueOf(limmaxAssoluto).matches(pattern_numerico)) {
            aggiudicazioneA2_29.setValoreSogliaAnomalia(limmaxAssoluto);
          }
        } else {
          //Errore pattern
        }

        aggiudicazioneA2_29.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        aggiudicazioneA2_29.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if (dattoa != null) {
          aggiudicazioneA2_29.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneA2_29.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        if(modlicg != null && !Long.valueOf(6).equals(modlicg)) {
          Double offertaMininmoRibasso = null;
          if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
            offertaMininmoRibasso = npaFunzioniManager.getOffertaMinimoRibasso(codGar, ngara);
          aggiudicazioneA2_29.setDatiBaseSottomissioniRicevute(new DatiBaseSottomissioniRicevute(offertaMininmoRibasso, npaFunzioniManager.getOffertaMassimoRibasso(codGar, ngara)));
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA2_29.setOffertePresentate(datiOfferte);
        }

        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA2_29.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA2_29.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA2_29.setNumeroOfferteAmmesse(nofval);
        }
        
        
        listaAggiudicazioneA2_29.add(aggiudicazioneA2_29);
      }
      anacFormA2_29.setAggiudicazioni(listaAggiudicazioneA2_29);
    }


    String json = ow.writeValueAsString(anacFormA2_29);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, listaCodiciLotto);


  }

  private void registraJSON_A1_36(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA1_36 anacFormA1_36 = new AnacFormA1_36();

    String getDatiTorn = "select tipoconc from TORN where codgar = ? ";

    Long tipoconc = (Long) this.sqlManager.getObject(getDatiTorn, new Object[]{codGar});

    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA1_36> listaAggiudicazioneA1_36 = new ArrayList<AggiudicazioneA1_36>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA1_36 aggiudicazioneA1_36 = new AggiudicazioneA1_36();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();

        if(codcig != null) {
          aggiudicazioneA1_36.setCig(codcig);
        }

        String newCodGar = codGar;
        String newNgara = ngara;

        if(tipoconc != null && Long.valueOf(2).equals(tipoconc)) {
          String newDatiSQL = "select codgar1, ngara from gare where ngarapa = ?";
          Vector<?> newDati = this.sqlManager.getVector(newDatiSQL,new Object[]{ngara});
          if(newDati!=null && !newDati.isEmpty()){
            newCodGar = (String) SqlManager.getValueFromVectorParam(newDati, 0).getValue();
            newNgara = (String) SqlManager.getValueFromVectorParam(newDati, 1).getValue();
          }
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          if(Long.valueOf(4).equals(esineg)) {
            esineg = Long.valueOf(6);
          } else {
            esineg = Long.valueOf(7);
          }
          aggiudicazioneA1_36.setEsitoProceduraAnnullata(new EnumNPA("esitoProceduraAnnullata", String.valueOf(esineg)));
          proceduraAnnullata = Boolean.TRUE;
        }
        
        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA1_36.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA1_36.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA1_36.setNumeroOfferteAmmesse(nofval);
        }
        
        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(newCodGar, newNgara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA1_36.setOffertePresentate(datiOfferte);
        }
        
        listaAggiudicazioneA1_36.add(aggiudicazioneA1_36);
      }
      anacFormA1_36.setAggiudicazioni(listaAggiudicazioneA1_36);
    }

    String json = ow.writeValueAsString(anacFormA1_36);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P1_10(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    AnacFormP1_10 anacFormP1_10 = new AnacFormP1_10();
    List<String> listaCodiciLotto = new ArrayList<String>();

    anacFormP1_10.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP_10 appaltoP_10 = new AppaltoP_10();
    appaltoP_10.setCodiceAppalto(codiceAppaltoNpa);
    String getDatiTorn = "select terrid, sommaur, moturg, motcig, strumproc, idappaltonpa, idpianinpa from TORN where codgar = ? ";

    Vector<?> datiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(datiTorn!=null && !datiTorn.isEmpty()){
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue());
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue());
      Long moturg = (Long) SqlManager.getValueFromVectorParam(datiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(datiTorn, 3).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(datiTorn, 4).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 5).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 6).getValue();

      
      appaltoP_10.setIdPianificazione(idpianinpa);
      appaltoP_10.setTermineRidottoAvvisoPreinformazione(terrid);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP_10.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP_10.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP_10.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP_10.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));        
      }
      if(strumproc != null) {
        appaltoP_10.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
    }
    appaltoP_10.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    anacFormP1_10.setAppalto(appaltoP_10);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP_10> listalotti = new ArrayList<LottoP_10>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP_10 lotto = new LottoP_10();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        listaCodiciLotto.add(ngara);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }
        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }
        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }
        lotto.setCcnl(codcnel);
        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        lotto.setServizioPubblicoLocale(serviloc);

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(oggcont != null) {
          if(Long.valueOf(2).equals(oggcont) || Long.valueOf(3).equals(oggcont)) {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "2"));
          } else {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "1"));
          }
        }

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));


        listalotti.add(lotto);
      }
      anacFormP1_10.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_10);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);


  }


  private void registraJSON_P1_11(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP1_11 anacFormP1_11 = new AnacFormP1_11();
    List<String> listaCodiciLotto = new ArrayList<String>();

    anacFormP1_11.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP_11 appaltoP_11 = new AppaltoP_11();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP_11.setCodiceAppalto(codiceAppaltoNpa);
    String getDatiTorn = "select terrid, sommaur, moturg, motcig, strumproc, idappaltonpa, idpianinpa from TORN where codgar = ? ";

    Vector<?> datiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(datiTorn!=null && !datiTorn.isEmpty()){
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue());
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue());
      Long moturg = (Long) SqlManager.getValueFromVectorParam(datiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(datiTorn, 3).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(datiTorn, 4).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 5).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 6).getValue();

      
      appaltoP_11.setIdPianificazione(idpianinpa);
      appaltoP_11.setTermineRidottoAvvisoPreinformazione(terrid);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP_11.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP_11.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP_11.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP_11.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));        
      }
      if(strumproc != null) {
        appaltoP_11.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
    }
    appaltoP_11.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    anacFormP1_11.setAppalto(appaltoP_11);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP_11> listalotti = new ArrayList<LottoP_11>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP_11 lotto = new LottoP_11();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));


        listaCodiciLotto.add(ngara);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setCcnl(codcnel);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(oggcont != null) {
          if(Long.valueOf(2).equals(oggcont) || Long.valueOf(3).equals(oggcont)) {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "2"));
          } else {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "1"));
          }
        }

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));


        listalotti.add(lotto);
      }
      anacFormP1_11.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_11);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);


  }

  private void registraJSON_P2_16(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_16 anacFormP2_16 = new AnacFormP2_16();

    anacFormP2_16.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_16 appaltoP2_16 = new AppaltoP2_16();
    appaltoP2_16.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP2_16.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.moturg, t.sommaur, t.strumproc, t.motcig, t.idappaltonpa, "
        + "t.accqua, t.ricastae, t.prourg, t.motacc, t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 5);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 6);
      Boolean prourg = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue());
      String motacc = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 12).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 13).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 14).getValue();

      
      appaltoP2_16.setTermineRidottoAvvisoPreinformazione(terrid);
      appaltoP2_16.setIdPianificazione(idpianinpa);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP2_16.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP2_16.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP2_16.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP2_16.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }
      if(strumproc != null) {
        appaltoP2_16.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      DatiBaseProceduraCompleto baseProcedura = new DatiBaseProceduraCompleto();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));

      if(prourg != null && Boolean.TRUE.equals(prourg)) {
        baseProcedura.setProceduraAccelerata(Boolean.TRUE);
      } else {
        baseProcedura.setProceduraAccelerata(Boolean.FALSE);
      }

      if(motacc != null) {
        baseProcedura.setGiustificazioneProceduraAccelerata(motacc);
      }
      appaltoP2_16.setDatiBaseProcedura(baseProcedura);

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_16.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_16.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP2_16.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }


    anacFormP2_16.setAppalto(appaltoP2_16);



    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_16> listalotti = new ArrayList<LottoP2_16>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_16 lotto = new LottoP2_16();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));


        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }
        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(oggcont != null) {
          if(Long.valueOf(2).equals(oggcont) || Long.valueOf(3).equals(oggcont)) {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "2"));
          } else {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "1"));
          }
        }

        lotto.setServizioPubblicoLocale(serviloc);

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setCcnl(codcnel);

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));



        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        listalotti.add(lotto);
      }
      anacFormP2_16.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_16);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }


  private void registraJSON_P2_11(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_11 anacFormP2_11 = new AnacFormP2_11();

    anacFormP2_11.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_11 appaltoP2_11 = new AppaltoP2_11();
    appaltoP2_11.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP2_11.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.moturg, t.sommaur, t.strumproc, t.motcig, t.idappaltonpa, "
        + "t.accqua, t.ricastae, t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 5);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 6);
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 12).getValue();
      
      appaltoP2_11.setTermineRidottoAvvisoPreinformazione(terrid);
      appaltoP2_11.setIdPianificazione(idpianinpa);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP2_11.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP2_11.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP2_11.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP2_11.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }
      if(strumproc != null) {
        appaltoP2_11.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP2_11.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_11.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_11.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP2_11.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }


    anacFormP2_11.setAppalto(appaltoP2_11);



    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_11> listalotti = new ArrayList<LottoP2_11>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_11 lotto = new LottoP2_11();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }
        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(oggcont != null) {
          if(Long.valueOf(2).equals(oggcont) || Long.valueOf(3).equals(oggcont)) {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "2"));
          } else {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "1"));
          }
        }

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setCcnl(codcnel);

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));



        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvioSoloScadenza(npaFunzioniManager.unisciDataeOra(dtepar, otepar)));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        listalotti.add(lotto);
      }
      anacFormP2_11.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_11);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);



  }

  private void registraJSON_P2_10(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_10 anacFormP2_10 = new AnacFormP2_10();
    List<String> listaCodiciLotto = new ArrayList<String>();
    anacFormP2_10.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_10 appaltoP2_10 = new AppaltoP2_10();
    appaltoP2_10.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP2_10.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.moturg, t.sommaur, t.strumproc, t.motcig, "
        + "t.idappaltonpa, t.accqua, t.ricastae, "
        + "t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 5);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 6);
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 12).getValue();

      
      appaltoP2_10.setIdPianificazione(idpianinpa);
      appaltoP2_10.setTermineRidottoAvvisoPreinformazione(terrid);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP2_10.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP2_10.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP2_10.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP2_10.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }
      if(strumproc != null) {
        appaltoP2_10.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_10.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_10.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      appaltoP2_10.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP2_10.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }
    anacFormP2_10.setAppalto(appaltoP2_10);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_10> listalotti = new ArrayList<LottoP2_10>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_10 lotto = new LottoP2_10();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        listaCodiciLotto.add(ngara);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }
        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(oggcont != null) {
          if(Long.valueOf(2).equals(oggcont) || Long.valueOf(3).equals(oggcont)) {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "2"));
          } else {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "1"));
          }
        }

        lotto.setServizioPubblicoLocale(serviloc);

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));


        lotto.setCcnl(codcnel);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvioSoloScadenza(npaFunzioniManager.unisciDataeOra(dtepar, otepar)));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        listalotti.add(lotto);
      }
      anacFormP2_10.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_10);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);


  }

  private void registraJSON_P1_23(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP1_23 anacFormP1_23 = new AnacFormP1_23();
    List<String> listaCodiciLotto = new ArrayList<String>();
    anacFormP1_23.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP_23 appaltoP_23 = new AppaltoP_23();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP_23.setCodiceAppalto(codiceAppaltoNpa);

    String getDatiTorn = "select t.terrid, t.strumproc, t.idappaltonpa, t.idpianinpa "
        + "from TORN t "
        + "where t.codgar = ? ";

    Vector<?> datiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(datiTorn!=null && !datiTorn.isEmpty()){
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue());
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 2).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 3).getValue();

      Long dispopart = npaFunzioniManager.getDispopartByCodgarSql(codGar);

      
      appaltoP_23.setIdPianificazione(idpianinpa);
      appaltoP_23.setTermineRidottoAvvisoPreinformazione(terrid);
      if(dispopart != null && !npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
        appaltoP_23.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
      }
      if(strumproc != null) {
        appaltoP_23.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
    }

    anacFormP1_23.setAppalto(appaltoP_23);

    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP_23> listalotti = new ArrayList<LottoP_23>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP_23 lotto = new LottoP_23();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long motesclu = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 31).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impproge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 32).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Long dispopartLotto = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        listaCodiciLotto.add(ngara);

        lotto.setStrumentiElettroniciSpecifici(isstrumel);
        
        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        lotto.setCui(codcui);
        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        if(motesclu != null) {
          lotto.setMotivoEsclusioneOrdinarioSpeciale(new EnumNPA("motivoEsclusioneOrdinarioSpeciale", String.valueOf(motesclu)));
        }

        lotto.setQuadroEconomicoConcorsiProgettazione(new QuadroEconomicoConcorsiProgettazione(impapp, impproge));

        if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopartLotto)));

        listalotti.add(lotto);
      }
      anacFormP1_23.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_23);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);
  }


  private void registraJSON_P1_24(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP1_24 anacFormP1_24 = new AnacFormP1_24();

    anacFormP1_24.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP_24 appaltoP_24 = new AppaltoP_24();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP_24.setCodiceAppalto(codiceAppaltoNpa);

    String getDatiTorn = "select t.terrid, t.strumproc, t.idappaltonpa, t.idpianinpa "
        + "from TORN t "
        + "where t.codgar = ? ";

    Vector<?> datiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(datiTorn!=null && !datiTorn.isEmpty()){
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue());
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 2).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 3).getValue();

      
      appaltoP_24.setIdPianificazione(idpianinpa);
      appaltoP_24.setTermineRidottoAvvisoPreinformazione(terrid);

      if(strumproc != null) {
        appaltoP_24.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
    }

    anacFormP1_24.setAppalto(appaltoP_24);

    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP_24> listalotti = new ArrayList<LottoP_24>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP_24 lotto = new LottoP_24();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long motesclu = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 31).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impproge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 32).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        
        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        if(motesclu != null) {
          lotto.setMotivoEsclusioneOrdinarioSpeciale(new EnumNPA("motivoEsclusioneOrdinarioSpeciale", String.valueOf(motesclu)));
        }

        lotto.setQuadroEconomicoConcorsiProgettazione(new QuadroEconomicoConcorsiProgettazione(impapp, impproge));

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }
        listalotti.add(lotto);
      }
      anacFormP1_24.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_24);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);
  }

  private void registraJSON_AD2_25(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormAD2_25 anacFormAD2_25 = new AnacFormAD2_25();
    anacFormAD2_25.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoAD2_25 appaltoAD2_25 = new AppaltoAD2_25();

    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    List<String> listaCodiciLotto = new ArrayList<String>();
    String getDatiTorn = "select t.moturg, t.sommaur, t.reluproc, t.urbasco, t.tipgar, "
        + "t.idappaltonpa, t.motaggidir, t.iterga, t.accqua, "
        + "g.not_gar, v.genere, t.destor "
        + "from torn t "
        + "inner join gare g on g.codgar1 = t.codgar "
        + "inner join v_gare_genere v on v.codgar = g.codgar1 "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      String motaggidir = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 8);
      String not_gar = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();

      
      appaltoAD2_25.setCodiceAppalto(codiceAppaltoNpa);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoAD2_25.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoAD2_25.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoAD2_25.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoAD2_25.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoAD2_25.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

      DatiBaseProcedura baseProcedura = new DatiBaseProcedura();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));

      List<EnumNPA> giustificazioniAggiudicazioneDiretta = new ArrayList<EnumNPA>();
      if(motaggidir != null) {
        giustificazioniAggiudicazioneDiretta.add(new EnumNPA("giustificazioniAggiudicazioneDiretta", npaFunzioniManager.getTab2d1ByTab2tip(motaggidir, "A1z22")));
      }
      baseProcedura.setGiustificazioniAggiudicazioneDiretta(giustificazioniAggiudicazioneDiretta);

      appaltoAD2_25.setDatiBaseProcedura(baseProcedura);

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico baseStrumentiProcedura = new DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico(parameters);
      appaltoAD2_25.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);

      appaltoAD2_25.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoAD2_25.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(destor, null));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoAD2_25.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(not_gar, null));          
        }
      }

    }
    anacFormAD2_25.setAppalto(appaltoAD2_25);



    //array aggiudicazioni
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneAD2_25> listaAggiudicazioni = new ArrayList<AggiudicazioneAD2_25>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneAD2_25 aggiudicazioneAD2_25 = new AggiudicazioneAD2_25();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        aggiudicazioneAD2_25.setStrumentiElettroniciSpecifici(isstrumel);
        
        aggiudicazioneAD2_25.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        listaCodiciLotto.add(ngara);
        aggiudicazioneAD2_25.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        aggiudicazioneAD2_25.setAfferenteInvestimentiPNRR(ispnrr);
        aggiudicazioneAD2_25.setCcnl(codcnel);
        aggiudicazioneAD2_25.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          aggiudicazioneAD2_25.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            aggiudicazioneAD2_25.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            aggiudicazioneAD2_25.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }


        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        aggiudicazioneAD2_25.setAcquisizioneCup(acquisizioneCup);
        aggiudicazioneAD2_25.setCupLotto(risultato.get(acquisizioneCup));

        aggiudicazioneAD2_25.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        aggiudicazioneAD2_25.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          aggiudicazioneAD2_25.setQuadroEconomicoConcessioni(null);

        //gestione partecipanti
        aggiudicazioneAD2_25.setPartecipanti(npaFunzioniManager.getListaPartecipADDiscrAQ(ngara));

        aggiudicazioneAD2_25.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        aggiudicazioneAD2_25.setDatiBase(new DatiBaseLotto(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, null));

        aggiudicazioneAD2_25.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        aggiudicazioneAD2_25.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        if (dattoa != null) {
          aggiudicazioneAD2_25.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneAD2_25.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        aggiudicazioneAD2_25.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "A"));

        listaAggiudicazioni.add(aggiudicazioneAD2_25);  
      }
      anacFormAD2_25.setAggiudicazioni(listaAggiudicazioni);

    }


    String json = ow.writeValueAsString(anacFormAD2_25);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);

  }

  private void registraJSON_AD2_28(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormAD2_28 anacFormAD2_28 = new AnacFormAD2_28();
    anacFormAD2_28.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoAD2_28 appaltoAD2_28 = new AppaltoAD2_28();
    String uuid = UUID.randomUUID().toString();
    String codiceAppaltoNpa = npaFunzioniManager.togliDollaro(codGar)+"-"+uuid;
    List<String> listaCodiciLotto = new ArrayList<String>();
    String getDatiTorn = "select t.moturg, t.sommaur, t.urbasco, "
        + "t.motaggidir, t.iterga, g.not_gar, v.genere, t.destor, t.settore "
        + "from torn t "
        + "inner join gare g on g.codgar1 = t.codgar "
        + "inner join v_gare_genere v on v.codgar = g.codgar1 "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      String motaggidir = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      String not_gar = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();

      
      appaltoAD2_28.setCodiceAppalto(codiceAppaltoNpa);

      appaltoAD2_28.setSettore(NpaFunzioniManager.getEnumSettore(settore));

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoAD2_28.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoAD2_28.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoAD2_28.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoAD2_28.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

      DatiBaseProcedura baseProcedura = new DatiBaseProcedura();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));

      List<EnumNPA> giustificazioniAggiudicazioneDiretta = new ArrayList<EnumNPA>();
      if(motaggidir != null) {
        giustificazioniAggiudicazioneDiretta.add(new EnumNPA("giustificazioniAggiudicazioneDiretta", npaFunzioniManager.getTab2d1ByTab2tip(motaggidir, "A1z22")));
      }
      baseProcedura.setGiustificazioniAggiudicazioneDiretta(giustificazioniAggiudicazioneDiretta);

      appaltoAD2_28.setDatiBaseProcedura(baseProcedura);

      appaltoAD2_28.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoAD2_28.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(destor, null));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoAD2_28.setDatiBase(new DatiBaseAppaltoOptionalSoloOggetto(not_gar, null));          
        }
      }

    }
    anacFormAD2_28.setAppalto(appaltoAD2_28);



    //array aggiudicazioni
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneAD2_28> listaAggiudicazioni = new ArrayList<AggiudicazioneAD2_28>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneAD2_28 aggiudicazioneAD2_28 = new AggiudicazioneAD2_28();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        
        aggiudicazioneAD2_28.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        aggiudicazioneAD2_28.setStrumentiElettroniciSpecifici(isstrumel);

        listaCodiciLotto.add(ngara);
        aggiudicazioneAD2_28.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        aggiudicazioneAD2_28.setAfferenteInvestimentiPNRR(ispnrr);
        aggiudicazioneAD2_28.setCcnl(codcnel);
        aggiudicazioneAD2_28.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          aggiudicazioneAD2_28.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            aggiudicazioneAD2_28.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            aggiudicazioneAD2_28.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }


        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        aggiudicazioneAD2_28.setAcquisizioneCup(acquisizioneCup);
        aggiudicazioneAD2_28.setCupLotto(risultato.get(acquisizioneCup));

        aggiudicazioneAD2_28.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          aggiudicazioneAD2_28.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge,
                  impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));
        }

        aggiudicazioneAD2_28.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));

        //gestione partecipanti
        aggiudicazioneAD2_28.setPartecipanti(npaFunzioniManager.getListaPartecipADDiscrAQ(ngara));

        aggiudicazioneAD2_28.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        aggiudicazioneAD2_28.setDatiBase(new DatiBaseLotto(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, null));

        aggiudicazioneAD2_28.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        aggiudicazioneAD2_28.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        if (dattoa != null) {
          aggiudicazioneAD2_28.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneAD2_28.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        aggiudicazioneAD2_28.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "A"));

        listaAggiudicazioni.add(aggiudicazioneAD2_28);  
      }
      anacFormAD2_28.setAggiudicazioni(listaAggiudicazioni);

    }


    String json = ow.writeValueAsString(anacFormAD2_28);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);

  }
  
  private void registraJSON_A1_29(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA1_29 anacFormA1_29 = new AnacFormA1_29();
    AppaltoA1_29 appaltoA1_29 = new AppaltoA1_29();
    List<String> listaCodiciLotto = new ArrayList<String>();
    Long tipgen = null;

    String getDatiTorn = "select moturg, sommaur, reluproc, urbasco, tipgen from torn where codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      tipgen = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoA1_29.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoA1_29.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoA1_29.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoA1_29.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoA1_29.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

      appaltoA1_29.setModalitaRiaggiudicazioneAffidamento(null);

    }
    anacFormA1_29.setAppalto(appaltoA1_29);

    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA1_29> listaAggiudicazioneA1_29 = new ArrayList<AggiudicazioneA1_29>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA1_29 aggiudicazioneA1_29 = new AggiudicazioneA1_29();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Double limmax = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 6).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();

        listaCodiciLotto.add(ngara);

        if(codcig != null) {
          aggiudicazioneA1_29.setCig(codcig);
        }

        if(limmax != null) {
          //recupero il valore assoluto
          Double limmaxAssoluto = Math.abs(limmax);
          if(String.valueOf(limmaxAssoluto).matches(pattern_numerico)) {
            aggiudicazioneA1_29.setValoreSogliaAnomalia(limmaxAssoluto);
          }
        } else {
          //Errore pattern
        }
        
        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          if(Long.valueOf(4).equals(esineg)) {
            esineg = Long.valueOf(6);
          } else {
            esineg = Long.valueOf(7);
          }
          aggiudicazioneA1_29.setEsitoProceduraAnnullata(new EnumNPA("esitoProceduraAnnullata", String.valueOf(esineg)));
          proceduraAnnullata = Boolean.TRUE;
        }
        
        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA1_29.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA1_29.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA1_29.setNumeroOfferteAmmesse(nofval);
        }
        
        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA1_29.setOffertePresentate(datiOfferte);
        }

        aggiudicazioneA1_29.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        listaAggiudicazioneA1_29.add(aggiudicazioneA1_29);
      }
      anacFormA1_29.setAggiudicazioni(listaAggiudicazioneA1_29);
    }


    String json = ow.writeValueAsString(anacFormA1_29);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, listaCodiciLotto);

  }

  private void registraJSON_P1_16(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP1_16 anacFormP1_16 = new AnacFormP1_16();
    List<String> listaCodiciLotto = new ArrayList<String>();

    anacFormP1_16.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP_16 appaltoP_16 = new AppaltoP_16();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP_16.setCodiceAppalto(codiceAppaltoNpa);

    String getDatiTorn = "select terrid, sommaur, moturg, motcig, strumproc, idappaltonpa, idpianinpa from TORN where codgar = ? ";

    Vector<?> datiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(datiTorn!=null && !datiTorn.isEmpty()){
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue());
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue());
      Long moturg = (Long) SqlManager.getValueFromVectorParam(datiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(datiTorn, 3).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(datiTorn, 4).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 5).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 6).getValue();

      
      appaltoP_16.setIdPianificazione(idpianinpa);
      appaltoP_16.setTermineRidottoAvvisoPreinformazione(terrid);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP_16.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP_16.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP_16.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP_16.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }
      if(strumproc != null) {
        appaltoP_16.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
    }
    appaltoP_16.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    anacFormP1_16.setAppalto(appaltoP_16);

    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP_16> listalotti = new ArrayList<LottoP_16>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP_16 lotto = new LottoP_16();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        listaCodiciLotto.add(ngara);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setCcnl(codcnel);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(oggcont != null) {
          if(Long.valueOf(2).equals(oggcont) || Long.valueOf(3).equals(oggcont)) {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "2"));
          } else {
            lotto.setPrestazioniComprese(new EnumNPA("prestazioniComprese", "1"));
          }
        }

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }
        lotto.setServizioPubblicoLocale(serviloc);

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara) ));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));


        listalotti.add(lotto);
      }
      anacFormP1_16.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_16);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);

  }

  private void registraJSON_NAG(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormNAG anacFormNAG = new AnacFormNAG();

    String getDatiVgareGenere = "select g.esineg, g.codcig, g1.giustneg "
        + "from gare g "
        + "inner join G1NPALOTTISCHEDA ls on g.ngara = ls.ngara and ls.idscheda = ? "
        + "inner join gare1 g1 on ls.ngara = g1.ngara "
        + "where g.codgar1 = ? ";
    List<String> datiGareNAG = this.sqlManager.getListVector(getDatiVgareGenere, new Object[]{idscheda,codGar});
    if(datiGareNAG!=null && !datiGareNAG.isEmpty()){
      List<LottoNAG> listaLotti = new ArrayList<LottoNAG>();
      for (int i = 0; i < datiGareNAG.size(); i++) {
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiGareNAG.get(i), 0).getValue();
        String cig = (String) SqlManager.getValueFromVectorParam(datiGareNAG.get(i), 1).getValue();
        String giustificazione = SqlManager.getValueFromVectorParam(datiGareNAG.get(i), 2).getStringValue();

        LottoNAG lottoNAG = new LottoNAG();
        lottoNAG.setCig(cig);

        if (StringUtils.isNotEmpty(giustificazione)) {
          lottoNAG.setDatiBaseRisultatoProcedura(new DatiBaseRisultatoProcedura2(new EnumNPA("giustificazione", npaFunzioniManager.getTab2d1ByTab2tip(giustificazione, "A1z27"))));
        } else {
          lottoNAG.setDatiBaseRisultatoProcedura(new DatiBaseRisultatoProcedura2(null));
        }

        if(esineg != null) {
          if(Long.valueOf(4).equals(esineg)) {
            esineg = Long.valueOf(6);
          } else {
            esineg = Long.valueOf(7);
          }
          lottoNAG.setEsitoProceduraAnnullata(new EnumNPA("esitoProceduraAnnullata", String.valueOf(esineg)));
        }
        listaLotti.add(lottoNAG);
      }
      anacFormNAG.setLotti(listaLotti);
    }

    String json = ow.writeValueAsString(anacFormNAG);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);      
  }

  private List<ElencoSoggetti> popolaElencoSoggettiS2(List<String> listaCodiciLotto, String codGar,boolean filtroAggiudicazioni) throws Exception {
    List<String> datiVgareGenere = npaFunzioniManager.getDatiVgareGenereSQL(codGar);
    List<ElencoSoggetti> elencoSoggetti = null;
    if(datiVgareGenere!=null && !datiVgareGenere.isEmpty()){

      elencoSoggetti = new ArrayList<>();
      for (int i = 0; i < datiVgareGenere.size(); i++) {
        ElencoSoggetti soggetti = new ElencoSoggetti();
        String codiceLotto = (String) SqlManager.getValueFromVectorParam(datiVgareGenere.get(i), 0).getValue();

        soggetti.setCig(npaFunzioniManager.getCigDaGaraSQL(codiceLotto));
        listaCodiciLotto.add(codiceLotto);

        Long iterga = (Long) this.sqlManager.getObject("select ITERGA from torn where codgar = ?", new Object[]{codGar});
        if(iterga != null && !Long.valueOf(1).equals(iterga) && !Long.valueOf(9).equals(iterga)) {
          String getDatiDitG = "select dittao "
                  + "from DITG "
                  + "where (invgar is null or invgar = '1') "
                  + "and (fasgar is not null and fasgar = 1) "
                  + "and rtofferta is null "
                  + "and codgar5 = ? "
                  + "and ngara5 = ? "
                  + "and "+(filtroAggiudicazioni? "ngara5 not in "+ GestoreG1NPAFORM.WHERE_LOTTI_SENZA_SCHEDA:"codgar5=? ")
                  + "order by numordpl ";
          List<String> datiDitG = this.sqlManager.getListVector(getDatiDitG, new Object[]{codGar,codiceLotto, codGar});
          if(datiDitG!=null && !datiDitG.isEmpty()){
            List<Invitato> listaInvitatiCheNonHannoPresentatoOfferta = new ArrayList<Invitato>();
            for (int y = 0; y < datiDitG.size(); y++) {
              String codiceDitta = (String) SqlManager.getValueFromVectorParam(datiDitG.get(y), 0).getValue();
              Vector<?> datiImpr = npaFunzioniManager.getDatiImprSQL(codiceDitta);
              if(datiImpr!=null && !datiImpr.isEmpty()){
                Invitato invitatoCheNonHaPresentatoOfferta = new Invitato();
                Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
                String nomest = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
                String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 2).getValue();

                if(tipimp != null) {
                  if(Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp)) {
                    List<String> datiRagimp = npaFunzioniManager.getDatiRagimpSQL(codiceDitta);
                    if(datiRagimp!=null && !datiRagimp.isEmpty()){
                      for (int x = 0; x < datiRagimp.size(); x++) {
                        invitatoCheNonHaPresentatoOfferta = new Invitato();
                        codiceDitta = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 0).getValue();
                        String impMan = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 1).getValue();

                        if(impMan != null && "1".equals(impMan))
                          invitatoCheNonHaPresentatoOfferta.setRuoloOE(new EnumNPA("ruoloOE", "1"));
                        else
                          invitatoCheNonHaPresentatoOfferta.setRuoloOE(new EnumNPA("ruoloOE", "2"));

                        Vector<?> datiImpreseDaRTI = npaFunzioniManager.getDatiImprSQL(codiceDitta);
                        if(datiImpreseDaRTI!=null && !datiImpreseDaRTI.isEmpty()){
                          tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 0).getValue();
                          nomest = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 1).getValue();
                          cfimp = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 2).getValue();
                          invitatoCheNonHaPresentatoOfferta.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimp));
                          invitatoCheNonHaPresentatoOfferta.setCodiceFiscale(cfimp);
                          invitatoCheNonHaPresentatoOfferta.setDenominazione(nomest);
                          //soggetto.setIdGruppo(idGruppo);
                        }
                        listaInvitatiCheNonHannoPresentatoOfferta.add(invitatoCheNonHaPresentatoOfferta);
                      }
                      //idGruppo++;
                    }
                  } else if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2")) && (Long.valueOf(2).equals(tipimp) || Long.valueOf(11).equals(tipimp))){
                    invitatoCheNonHaPresentatoOfferta.setCodiceFiscale(cfimp);
                    invitatoCheNonHaPresentatoOfferta.setDenominazione(nomest);
                    invitatoCheNonHaPresentatoOfferta.setRuoloOE(new EnumNPA("ruoloOE", "7"));
                    invitatoCheNonHaPresentatoOfferta.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimp));
                    listaInvitatiCheNonHannoPresentatoOfferta.add(invitatoCheNonHaPresentatoOfferta);
                  } else {
                    invitatoCheNonHaPresentatoOfferta.setCodiceFiscale(cfimp);
                    invitatoCheNonHaPresentatoOfferta.setDenominazione(nomest);
                    if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
                      invitatoCheNonHaPresentatoOfferta.setRuoloOE(new EnumNPA("ruoloOE", "3"));
                    } else {
                      invitatoCheNonHaPresentatoOfferta.setRuoloOE(new EnumNPA("ruoloOE", "1"));
                    }
                    invitatoCheNonHaPresentatoOfferta.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimp));
                    listaInvitatiCheNonHannoPresentatoOfferta.add(invitatoCheNonHaPresentatoOfferta);
                  }
                } else { // else di tipimp != null
                  invitatoCheNonHaPresentatoOfferta.setCodiceFiscale(cfimp);
                  invitatoCheNonHaPresentatoOfferta.setDenominazione(nomest);
                  if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
                    invitatoCheNonHaPresentatoOfferta.setRuoloOE(new EnumNPA("ruoloOE", "3"));
                  } else {
                    invitatoCheNonHaPresentatoOfferta.setRuoloOE(new EnumNPA("ruoloOE", "1"));
                  }
                  invitatoCheNonHaPresentatoOfferta.setTipoOE(npaFunzioniManager.calcolaTipoOE(Long.valueOf(1)));
                  listaInvitatiCheNonHannoPresentatoOfferta.add(invitatoCheNonHaPresentatoOfferta);
                }
              }
            }
            soggetti.setInvitatiCheNonHannoPresentatoOfferta(listaInvitatiCheNonHannoPresentatoOfferta);
          }
        }

        //Partecipanti
        String getPartecipantiDitG = "select dittao, idpartecipnpa "
                + "from DITG "
                + "where (fasgar is null or fasgar > 1)  "
                + "and codgar5 = ? "
                + "and ngara5 = ? "
                + "and invoff = '1' "
                + "and "+(filtroAggiudicazioni? "ngara5 not in "+GestoreG1NPAFORM.WHERE_LOTTI_SENZA_SCHEDA:"codgar5=? ")
                + "order by numordpl ";
        List<String> datiDitGPar = this.sqlManager.getListVector(getPartecipantiDitG, new Object[]{codGar,codiceLotto, codGar});
        if(datiDitGPar!=null && !datiDitGPar.isEmpty()){
          soggetti.setPartecipanti(npaFunzioniManager.getListaPartecipanti(datiDitGPar, codGar, codiceLotto));
        }

        String getDateTorn = "select DINVIT, DTEOFF, OTEOFF from TORN where codgar = ? ";
        Vector<?> vetDateTorn = this.sqlManager.getVector(getDateTorn, new Object[]{codGar});
        if(vetDateTorn!=null && !vetDateTorn.isEmpty()){
          String dataInvito = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(vetDateTorn, 0).getValue());
          String dataScadenzaPresentazioneOfferta = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(vetDateTorn, 1).getValue());
          String oraScadenzaPresentazioneOfferta = (String) SqlManager.getValueFromVectorParam(vetDateTorn, 2).getValue();
          String dataScadenza = npaFunzioniManager.unisciDataeOra(dataScadenzaPresentazioneOfferta,oraScadenzaPresentazioneOfferta);

          soggetti.setDataInvito(dataInvito);
          soggetti.setDataScadenzaPresentazioneOfferta(dataScadenza);
        }

        // Anac non vuole visualizzare il soggetto con il cig che non ha partecipanti
        if(soggetti.getPartecipanti() != null && !soggetti.getPartecipanti().isEmpty()) {
          elencoSoggetti.add(soggetti);
        }
      }

    }
    return elencoSoggetti;
  }

  private void registraJSON_S2(Long idscheda, String codGar, String codiceScheda, String ngara, String tipoWSERP) throws Exception {
    AnacFormS2 anacFormS2 = new AnacFormS2();
    List<String> listaCodiciLotto = new ArrayList<>();
    List<ElencoSoggetti> elencoSoggetti = popolaElencoSoggettiS2(listaCodiciLotto,codGar,false);
    if(elencoSoggetti != null && !elencoSoggetti.isEmpty()) {
      anacFormS2.setElencoSoggetti(elencoSoggetti);
    }

    String json = ow.writeValueAsString(anacFormS2);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    //String uuid = UUID.randomUUID().toString();
    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, listaCodiciLotto);

  }

  private void registraJSON_S2R(Long idscheda, String codGar, String codiceScheda, String ngara, String tipoWSERP) throws Exception {
    AnacFormS2R anacFormS2R = new AnacFormS2R();
    List<String> listaCodiciLotto = new ArrayList<>();
    List<ElencoSoggetti> elencoSoggetti = popolaElencoSoggettiS2(listaCodiciLotto,codGar,true);

    if(elencoSoggetti != null && !elencoSoggetti.isEmpty()) {
      anacFormS2R.setElencoSoggetti(elencoSoggetti);
    }
    anacFormS2R.setMotivoRichiestaRettifica(new EnumNPA("motivoRichiestaRettifica", "3"));


    String json = ow.writeValueAsString(anacFormS2R);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    //String uuid = UUID.randomUUID().toString();
    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, listaCodiciLotto);

  }

  private void registraJSON_S1(Long idscheda, String codGar, String codiceScheda, String ngara, String tipoWSERP) throws Exception {

    AnacFormS1 anacFormS1 = new AnacFormS1();
    List<String> listaCodiciLotto = new ArrayList<String>();
    Long genere = npaFunzioniManager.getGenereByCodgar(codGar);
    List<String> datiVgareGenere = npaFunzioniManager.getDatiVgareGenereSQL(codGar);
    if(datiVgareGenere!=null && !datiVgareGenere.isEmpty()){
      List<ElencoSoggettiRichiedenti> elencoSoggettiRichiedenti = new ArrayList<ElencoSoggettiRichiedenti>();
      for (int i = 0; i < datiVgareGenere.size(); i++) {
        ElencoSoggettiRichiedenti soggettiRichiedenti = new ElencoSoggettiRichiedenti();

        String codiceLotto = (String) SqlManager.getValueFromVectorParam(datiVgareGenere.get(i), 0).getValue();
        listaCodiciLotto.add(codiceLotto);
        soggettiRichiedenti.setCig(npaFunzioniManager.getCigDaGaraSQL(codiceLotto));

        String getDatiDitG = "select dittao, idpartecipnpa "
            + "from DITG "
            + "where codgar5 = ? "
            + "and ngara5 = ? "
            + "and (acquisizione is null or acquisizione <> 5) ";
        String ngaraDitgAvval = codiceLotto;
        if(Long.valueOf(3).equals(genere)) {
          getDatiDitG +="and (partgar is null or partgar = '1') ";
          ngaraDitgAvval = codGar;
        }
        getDatiDitG += "order by numordpl";
        List<String> datiDitG = this.sqlManager.getListVector(getDatiDitG, new Object[]{codGar,codiceLotto});
        if(datiDitG!=null && !datiDitG.isEmpty()){
          List<Soggetto> listaImprese = new ArrayList<Soggetto>();
          for (int y = 0; y < datiDitG.size(); y++) {
            String codiceDitta = (String) SqlManager.getValueFromVectorParam(datiDitG.get(y), 0).getValue();
            String idpartecipnpa = (String) SqlManager.getValueFromVectorParam(datiDitG.get(y), 1).getValue();
            Vector<?> datiImpr = npaFunzioniManager.getDatiImprSQL(codiceDitta);
            if(datiImpr!=null && !datiImpr.isEmpty()){
              Soggetto soggetto = new Soggetto();
              Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
              String nomest = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
              String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 2).getValue();

              if(tipimp != null) {
                if(Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp)) {
                  List<String> datiRagimp = npaFunzioniManager.getDatiRagimpSQL(codiceDitta);
                  if(datiRagimp!=null && !datiRagimp.isEmpty()){
                    for (int x = 0; x < datiRagimp.size(); x++) {
                      soggetto = new Soggetto();
                      String codiceDittaInt = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 0).getValue();
                      String impMan = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 1).getValue();

                      if(impMan != null && "1".equals(impMan))
                        soggetto.setRuoloOE(new EnumNPA("ruoloOE", "1"));
                      else
                        soggetto.setRuoloOE(new EnumNPA("ruoloOE", "2"));

                      Vector<?> datiImpreseDaRTI = npaFunzioniManager.getDatiImprSQL(codiceDittaInt);
                      if(datiImpreseDaRTI!=null && !datiImpreseDaRTI.isEmpty()){
                        tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 0).getValue();
                        nomest = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 1).getValue();
                        cfimp = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 2).getValue();

                        soggetto.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimp));

                        soggetto.setCodiceFiscale(cfimp);
                        soggetto.setDenominazione(nomest);
                        soggetto.setIdPartecipante(idpartecipnpa);
                      }
                      listaImprese.add(soggetto);
                    }
                  }
                } else if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2")) 
                    && (Long.valueOf(2).equals(tipimp) || Long.valueOf(11).equals(tipimp))){
                  soggetto.setCodiceFiscale(cfimp);
                  soggetto.setDenominazione(nomest);
                  soggetto.setRuoloOE(new EnumNPA("ruoloOE", "7"));
                  soggetto.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimp));
                  soggetto.setIdPartecipante(idpartecipnpa);
                  // ragimp figli tuolo 11 anche per S2
                  listaImprese.add(soggetto);
                  List<String> datiRagdet = npaFunzioniManager.getDatiRagdetSQL(codiceDitta, codGar, ngara, genere);
                  if(datiRagdet!=null && !datiRagdet.isEmpty()){
                    for (int x = 0; x < datiRagdet.size(); x++) {
                      soggetto = new Soggetto();
                      String codiceDittaInt = (String) SqlManager.getValueFromVectorParam(datiRagdet.get(x), 0).getValue();
                      
                      soggetto.setRuoloOE(new EnumNPA("ruoloOE", "11"));

                      Vector<?> datiImpreseDaConsorzio = npaFunzioniManager.getDatiImprSQL(codiceDittaInt);
                      if(datiImpreseDaConsorzio!=null && !datiImpreseDaConsorzio.isEmpty()){
                        tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaConsorzio, 0).getValue();
                        nomest = (String) SqlManager.getValueFromVectorParam(datiImpreseDaConsorzio, 1).getValue();
                        cfimp = (String) SqlManager.getValueFromVectorParam(datiImpreseDaConsorzio, 2).getValue();

                        soggetto.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimp));

                        soggetto.setCodiceFiscale(cfimp);
                        soggetto.setDenominazione(nomest);
                        soggetto.setIdPartecipante(idpartecipnpa);
                      }
                      listaImprese.add(soggetto);
                    }
                  }
                } else {
                  soggetto.setCodiceFiscale(cfimp);
                  soggetto.setDenominazione(nomest);
                  if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
                    soggetto.setRuoloOE(new EnumNPA("ruoloOE", "3"));
                  } else {
                    soggetto.setRuoloOE(new EnumNPA("ruoloOE", "1"));
                  }
                  soggetto.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimp));
                  soggetto.setIdPartecipante(idpartecipnpa);
                  listaImprese.add(soggetto);
                }
              } else { // else di tipimp != null
                soggetto.setCodiceFiscale(cfimp);
                soggetto.setDenominazione(nomest);
                if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
                  soggetto.setRuoloOE(new EnumNPA("ruoloOE", "3"));
                } else {
                  soggetto.setRuoloOE(new EnumNPA("ruoloOE", "1"));
                }
                soggetto.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimp));
                soggetto.setIdPartecipante(idpartecipnpa);
                listaImprese.add(soggetto);
              }
              //APPALTI-1821 aggiungo ditte ausiliarie (avvalimento)
              List<Vector>  datiImprAvvalim = sqlManager.getListVector("select i.tipimp, i.nomest, i.cfimp, i.nazimp from impr i, ditgavval d where i.codimp=d.dittaav and d.ngara=? and d.dittao=?", new Object[]{ngaraDitgAvval,codiceDitta});
              if(datiImprAvvalim!=null && !datiImprAvvalim.isEmpty()) {
                for (int j = 0; j < datiImprAvvalim.size(); j++) {
                  Soggetto soggettoAvv = new Soggetto();
                  Long tipimpAvv = (Long) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 0).getValue();
                  String nomestAvv = (String) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 1).getValue();
                  String cfimpAvv = (String) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 2).getValue();
                  soggettoAvv.setCodiceFiscale(cfimpAvv);
                  soggettoAvv.setDenominazione(nomestAvv);
                  soggettoAvv.setRuoloOE(new EnumNPA("ruoloOE", "4"));
                  soggettoAvv.setTipoOE(npaFunzioniManager.calcolaTipoOE(tipimpAvv));
                  soggettoAvv.setIdPartecipante(idpartecipnpa);
                  listaImprese.add(soggettoAvv);
                }
              }
            }
          }
          soggettiRichiedenti.setSoggettiInteressati(listaImprese);
        }
        elencoSoggettiRichiedenti.add(soggettiRichiedenti);
      }
      anacFormS1.setElencoSoggettiRichiedenti(elencoSoggettiRichiedenti);
    }

    String json = ow.writeValueAsString(anacFormS1);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    //String uuid = UUID.randomUUID().toString();
    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, listaCodiciLotto);
  }

  private void registraJSON_AD1_25(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormAD1_25 anacFormAD1_25 = new AnacFormAD1_25();
    anacFormAD1_25.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoAD1_25 appaltoAD1_25 = new AppaltoAD1_25();

    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    List<String> listaCodiciLotto = new ArrayList<String>();
    String getDatiTorn = "select t.moturg, t.sommaur, t.reluproc, t.urbasco, t.idappaltonpa "
        + "from torn t "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();

      
      appaltoAD1_25.setCodiceAppalto(codiceAppaltoNpa);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoAD1_25.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoAD1_25.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoAD1_25.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoAD1_25.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoAD1_25.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

    }
    anacFormAD1_25.setAppalto(appaltoAD1_25);



    //array aggiudicazioni
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneAD1_25> listaAggiudicazioni = new ArrayList<AggiudicazioneAD1_25>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneAD1_25 aggiudicazioneAD1_25 = new AggiudicazioneAD1_25();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        
        aggiudicazioneAD1_25.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        aggiudicazioneAD1_25.setStrumentiElettroniciSpecifici(isstrumel);

        listaCodiciLotto.add(ngara);
        aggiudicazioneAD1_25.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        aggiudicazioneAD1_25.setAfferenteInvestimentiPNRR(ispnrr);
        aggiudicazioneAD1_25.setCcnl(codcnel);
        aggiudicazioneAD1_25.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          aggiudicazioneAD1_25.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            aggiudicazioneAD1_25.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            aggiudicazioneAD1_25.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        aggiudicazioneAD1_25.setAcquisizioneCup(acquisizioneCup);
        aggiudicazioneAD1_25.setCupLotto(risultato.get(acquisizioneCup));

        aggiudicazioneAD1_25.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        aggiudicazioneAD1_25.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge,
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          aggiudicazioneAD1_25.setQuadroEconomicoConcessioni(null);
        }

        //gestione partecipanti
        aggiudicazioneAD1_25.setPartecipanti(npaFunzioniManager.getListaPartecipADDiscrAQ(ngara));

        aggiudicazioneAD1_25.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        listaAggiudicazioni.add(aggiudicazioneAD1_25);  
      }
      anacFormAD1_25.setAggiudicazioni(listaAggiudicazioni);

    }


    String json = ow.writeValueAsString(anacFormAD1_25);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);

  }


  private void registraJSON_AD1_28(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormAD1_28 anacFormAD1_28 = new AnacFormAD1_28();
    anacFormAD1_28.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoAD1_28 appaltoAD1_28 = new AppaltoAD1_28();

    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    List<String> listaCodiciLotto = new ArrayList<String>();
    String getDatiTorn = "select t.moturg, t.sommaur, t.urbasco, t.idappaltonpa, t.settore "
        + "from torn t "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();

      
      appaltoAD1_28.setCodiceAppalto(codiceAppaltoNpa);

      appaltoAD1_28.setSettore(NpaFunzioniManager.getEnumSettore(settore));

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoAD1_28.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoAD1_28.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoAD1_28.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoAD1_28.setOpereUrbanizzazioneScomputo(urbasco==null?Boolean.valueOf(false):urbasco);

    }
    anacFormAD1_28.setAppalto(appaltoAD1_28);



    //array aggiudicazioni
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneAD1_28> listaAggiudicazioni = new ArrayList<AggiudicazioneAD1_28>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneAD1_28 aggiudicazioneAD1_28 = new AggiudicazioneAD1_28();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        
        aggiudicazioneAD1_28.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        aggiudicazioneAD1_28.setStrumentiElettroniciSpecifici(isstrumel);
        
        listaCodiciLotto.add(ngara);
        aggiudicazioneAD1_28.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        aggiudicazioneAD1_28.setAfferenteInvestimentiPNRR(ispnrr);
        aggiudicazioneAD1_28.setCcnl(codcnel);
        aggiudicazioneAD1_28.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          aggiudicazioneAD1_28.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            aggiudicazioneAD1_28.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            aggiudicazioneAD1_28.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }


        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        aggiudicazioneAD1_28.setAcquisizioneCup(acquisizioneCup);
        aggiudicazioneAD1_28.setCupLotto(risultato.get(acquisizioneCup));

        aggiudicazioneAD1_28.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        if(!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          aggiudicazioneAD1_28.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge,
                  impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));
        }

        aggiudicazioneAD1_28.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));

        //gestione partecipanti
        aggiudicazioneAD1_28.setPartecipanti(npaFunzioniManager.getListaPartecipADDiscrAQ(ngara));

        aggiudicazioneAD1_28.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        listaAggiudicazioni.add(aggiudicazioneAD1_28);  
      }
      anacFormAD1_28.setAggiudicazioni(listaAggiudicazioni);

    }


    String json = ow.writeValueAsString(anacFormAD1_28);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);

  }

  private void registraJSON_P1_14(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP1_14 anacFormP1_14 = new AnacFormP1_14();

    anacFormP1_14.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP_14 appaltoP_14 = new AppaltoP_14();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP_14.setCodiceAppalto(codiceAppaltoNpa);
    String getDatiTorn = "select terrid, motcig, strumproc, idappaltonpa, idpianinpa, issocscopo, settore from TORN where codgar = ? ";

    Vector<?> datiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(datiTorn!=null && !datiTorn.isEmpty()){
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue());
      Long motcig = (Long) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(datiTorn, 2).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 3).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 4).getValue();
      Boolean issocscopo = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 5).getValue());
      String settore = (String) SqlManager.getValueFromVectorParam(datiTorn, 6).getValue();

      appaltoP_14.setSettore(NpaFunzioniManager.getEnumSettore(settore));
      
      appaltoP_14.setIdPianificazione(idpianinpa);
      appaltoP_14.setTermineRidottoAvvisoPreinformazione(terrid);

      if(motcig != null) {
        appaltoP_14.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));        
      }
      if(strumproc != null) {
        appaltoP_14.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
      if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
        appaltoP_14.setCostituzioneSocietaDiScopo(issocscopo);
    }
    appaltoP_14.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    anacFormP1_14.setAppalto(appaltoP_14);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP_14> listalotti = new ArrayList<LottoP_14>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP_14 lotto = new LottoP_14();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        lotto.setCcnl(codcnel);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        listalotti.add(lotto);
      }
      anacFormP1_14.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_14);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P1_19(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP1_19 anacFormP1_19 = new AnacFormP1_19();

    anacFormP1_19.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP_19 appaltoP_19 = new AppaltoP_19();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP_19.setCodiceAppalto(codiceAppaltoNpa);
    String getDatiTorn = "select terrid, motcig, strumproc, idappaltonpa, idpianinpa, issocscopo, settore from TORN where codgar = ? ";

    Vector<?> datiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(datiTorn!=null && !datiTorn.isEmpty()){
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue());
      Long motcig = (Long) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(datiTorn, 2).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 3).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 4).getValue();
      Boolean issocscopo = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 5).getValue());
      String settore = (String) SqlManager.getValueFromVectorParam(datiTorn, 6).getValue();

      appaltoP_19.setSettore(NpaFunzioniManager.getEnumSettore(settore));
      
      appaltoP_19.setIdPianificazione(idpianinpa);
      appaltoP_19.setTermineRidottoAvvisoPreinformazione(terrid);

      if(motcig != null) {
        appaltoP_19.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));        
      }
      if(strumproc != null) {
        appaltoP_19.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
      if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
        appaltoP_19.setCostituzioneSocietaDiScopo(issocscopo);
    }
    appaltoP_19.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    anacFormP1_19.setAppalto(appaltoP_19);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP_19> listalotti = new ArrayList<LottoP_19>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP_19 lotto = new LottoP_19();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));


        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        lotto.setCcnl(codcnel);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        listalotti.add(lotto);
      }
      anacFormP1_19.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_19);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P2_14(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_14 anacFormP2_14 = new AnacFormP2_14();

    anacFormP2_14.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_14 appaltoP2_14 = new AppaltoP2_14();

    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP2_14.setCodiceAppalto(codiceAppaltoNpa);
    appaltoP2_14.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    String gartel = null;

    String getDatiTorn = "select t.motcig, t.strumproc, t.idpianinpa, t.terrid, "
        + "t.accqua, t.ricastae, t.iterga, v.genere, t.destor, t.issocscopo, t.gartel, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 4);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 5);
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      Boolean issocscopo = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue());
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();

      appaltoP2_14.setSettore(NpaFunzioniManager.getEnumSettore(settore));


      if(motcig != null) {
        appaltoP2_14.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }

      if(strumproc != null) {
        appaltoP2_14.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP2_14.setIdPianificazione(idpianinpa);

      appaltoP2_14.setTermineRidottoAvvisoPreinformazione(terrid);


      appaltoP2_14.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_14.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_14.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }


      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProceduraOptional baseStrumentiProcedura = new DatiBaseStrumentiProceduraOptional(parameters);
      appaltoP2_14.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);

      if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
        appaltoP2_14.setCostituzioneSocietaDiScopo(issocscopo);

    }


    anacFormP2_14.setAppalto(appaltoP2_14);



    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_14> listalotti = new ArrayList<LottoP2_14>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_14 lotto = new LottoP2_14();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        
        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setCcnl(codcnel);

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));


        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvioSoloScadenza(npaFunzioniManager.unisciDataeOra(dtepar, otepar)));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        listalotti.add(lotto);
      }
      anacFormP2_14.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_14);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P2_19(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_19 anacFormP2_19 = new AnacFormP2_19();

    anacFormP2_19.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_19 appaltoP2_19 = new AppaltoP2_19();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);

    appaltoP2_19.setCodiceAppalto(codiceAppaltoNpa);
    appaltoP2_19.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    String gartel = null;

    String getDatiTorn = "select t.motcig, t.strumproc, t.idpianinpa, t.terrid, "
        + "t.accqua, t.ricastae, t.iterga, v.genere, t.destor, t.issocscopo, t.gartel, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 5);
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      Boolean issocscopo = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue());
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();

      appaltoP2_19.setSettore(NpaFunzioniManager.getEnumSettore(settore));


      if(motcig != null) {
        appaltoP2_19.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }

      if(strumproc != null) {
        appaltoP2_19.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP2_19.setIdPianificazione(idpianinpa);

      appaltoP2_19.setTermineRidottoAvvisoPreinformazione(terrid);

      appaltoP2_19.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_19.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_19.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProceduraOnlyAsta baseStrumentiProcedura = new DatiBaseStrumentiProceduraOnlyAsta(parameters);
      appaltoP2_19.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);

      if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
        appaltoP2_19.setCostituzioneSocietaDiScopo(issocscopo);
    }


    anacFormP2_19.setAppalto(appaltoP2_19);



    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_19> listalotti = new ArrayList<LottoP2_19>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_19 lotto = new LottoP2_19();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));


        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setCcnl(codcnel);

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        listalotti.add(lotto);
      }
      anacFormP2_19.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_19);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P6_1(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormP6_1 anacFormP6_1 = new AnacFormP6_1();
    anacFormP6_1.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP6_1 appaltoP6_1 = new AppaltoP6_1();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP6_1.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.strumproc, t.idappaltonpa, t.terrid, t.iterga, "
        + "v.genere, t.destor, t.idpianinpa, t.prourg, t.motacc, t.accqua, t.ricastae, t.codcigaq, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      Boolean prourg = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue());
      String motacc = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 9);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 10);
      String codcigaq = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 12).getValue();

      appaltoP6_1.setCigAccordoQuadro(codcigaq);
      if(strumproc != null) {
        appaltoP6_1.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
      appaltoP6_1.setIdPianificazione(idpianinpa);

      appaltoP6_1.setTermineRidottoAvvisoPreinformazione(terrid);

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP6_1.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP6_1.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      DatiBaseProceduraCompleto baseProcedura = new DatiBaseProceduraCompleto();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));
      if(prourg != null && Boolean.TRUE.equals(prourg)) {
        baseProcedura.setProceduraAccelerata(Boolean.TRUE);
      } else {
        baseProcedura.setProceduraAccelerata(Boolean.FALSE);
      }
      if(motacc != null) {
        baseProcedura.setGiustificazioneProceduraAccelerata(motacc);
      }
      appaltoP6_1.setDatiBaseProcedura(baseProcedura);

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProceduraOptional baseStrumentiProcedura = new DatiBaseStrumentiProceduraOptional(parameters);
      appaltoP6_1.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }
    anacFormP6_1.setAppalto(appaltoP6_1);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP6_1> listalotti = new ArrayList<LottoP6_1>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP6_1 lotto = new LottoP6_1();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        lotto.setStrumentiElettroniciSpecifici(isstrumel);
        
        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        lotto.setCui(codcui);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }


        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvioSoloOra(npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        listalotti.add(lotto);
      }
      anacFormP6_1.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP6_1);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);


  }


  private void registraJSON_P4_5(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormP4_5 anacFormP4_5 = new AnacFormP4_5();
    anacFormP4_5.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP4_5 appaltoP4_5 = new AppaltoP4_5();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP4_5.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.strumproc, t.terrid, t.iterga, "
        + "v.genere, t.destor, t.idpianinpa, t.ricastae, t.isprelprom, t.issocscopo, t.gartel, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 6);
      Boolean isprelprom = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue());
      Boolean issocscopo = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue());
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();

      appaltoP4_5.setSettore(NpaFunzioniManager.getEnumSettore(settore));

      if(strumproc != null) {
        appaltoP4_5.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
      appaltoP4_5.setIdPianificazione(idpianinpa);

      appaltoP4_5.setTermineRidottoAvvisoPreinformazione(terrid);

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP4_5.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP4_5.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      appaltoP4_5.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProceduraOnlyAsta baseStrumentiProcedura = new DatiBaseStrumentiProceduraOnlyAsta(parameters);
      appaltoP4_5.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);

      appaltoP4_5.setPrelazionePromotore(isprelprom);

      appaltoP4_5.setCostituzioneSocietaDiScopo(issocscopo);
    }    
    anacFormP4_5.setAppalto(appaltoP4_5);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP4_5> listalotti = new ArrayList<LottoP4_5>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP4_5 lotto = new LottoP4_5();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tiplav = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 26).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));


        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCcnl(codcnel);

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(tiplav != null) {
          List<EnumNPA> tipologiaLavoro = new ArrayList<EnumNPA>();
          EnumNPA tipLavEnumNPA = new EnumNPA();
          tipLavEnumNPA.setIdTipologica("tipologiaLavoro");
          tipLavEnumNPA.setCodice(String.valueOf(tiplav));
          tipologiaLavoro.add(tipLavEnumNPA);
          lotto.setTipologiaLavoro(tipologiaLavoro);
        }


        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setStrumentiElettroniciSpecifici(isstrumel);


        listalotti.add(lotto);
      }
      anacFormP4_5.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP4_5);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);


  }

  private void registraJSON_P5(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormP5 anacFormP5 = new AnacFormP5();
    anacFormP5.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP5 appaltoP5 = new AppaltoP5();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP5.setCodiceAppalto(codiceAppaltoNpa);

    String getDatiTorn = "select v.genere, t.destor, t.codcigaq, t.settore "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      String codcigaq = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      String settore = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();

      appaltoP5.setSettore(NpaFunzioniManager.getEnumSettore(settore));
      appaltoP5.setCigAccordoQuadro(codcigaq);
      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP5.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP5.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }
      
    }    
    anacFormP5.setAppalto(appaltoP5);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP5> listalotti = new ArrayList<LottoP5>();
      for (int i = 0; i < datiLotti.size(); i++) {
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long fattra = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 61).getValue();
        Boolean accqua = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 29).getValue());

        LottoP5 lotto = null;
        if (npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto =new LottoP5<List<String>>();

          Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
          boolean acquisizioneCup = risultato.keySet().iterator().next();
          lotto.setCup(risultato.get(acquisizioneCup));
        } else {
          lotto = new LottoP5<String>();

          lotto.setCup(cupprg);
        }

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));
        
        if(fattra != null)
          lotto.setFattispecieTracciabilita(new EnumNPA("fattispecieTracciabilita", String.valueOf(fattra)));
        
        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));
        
        lotto.setSceltaContraente(new EnumNPA("sceltaContraente", npaFunzioniManager.getSceltaContraente(accqua, isadesione, ngara, iterga)));

        listalotti.add(lotto);
      }
      anacFormP5.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP5);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);


  }
  
  private void registraJSON_A1_32(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA1_32 anacFormA1_32 = new AnacFormA1_32();
    AppaltoA1_32 appaltoA1_32 = new AppaltoA1_32();
    List<String> listaCodiciLotto = new ArrayList<String>();

    /*
   String getDatiTorn = "select moturg, sommaur, reluproc, urbasco, tipgen from torn where codgar = ? ";
   Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
   if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
     Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
     Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
     Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
     Boolean urbasco = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
     Long tipgen = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();

     appaltoA1_32.setModalitaRiaggiudicazioneAffidamento(null);

   }
     */
    anacFormA1_32.setAppalto(appaltoA1_32);

    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA1_32> listaAggiudicazioneA1_32 = new ArrayList<AggiudicazioneA1_32>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA1_32 aggiudicazioneA1_32 = new AggiudicazioneA1_32();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();

        listaCodiciLotto.add(ngara);

        if(codcig != null) {
          aggiudicazioneA1_32.setCig(codcig);
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          if(Long.valueOf(4).equals(esineg)) {
            esineg = Long.valueOf(6);
          } else {
            esineg = Long.valueOf(7);
          }
          aggiudicazioneA1_32.setEsitoProceduraAnnullata(new EnumNPA("esitoProceduraAnnullata", String.valueOf(esineg)));
          proceduraAnnullata = Boolean.TRUE;
        }
        
        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA1_32.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA1_32.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA1_32.setNumeroOfferteAmmesse(nofval);
        }
        
        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA1_32.setOffertePresentate(datiOfferte);
        }

        aggiudicazioneA1_32.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));

        aggiudicazioneA1_32.setnImpreseEscluseInsufficientiGiustificazioni(npaFunzioniManager.getNumeroImpreseEsclusePerInsufficientiGiustificazioni(codGar,ngara));

        listaAggiudicazioneA1_32.add(aggiudicazioneA1_32);
      }
      anacFormA1_32.setAggiudicazioni(listaAggiudicazioneA1_32);
    }


    String json = ow.writeValueAsString(anacFormA1_32);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, listaCodiciLotto);

  }


  private void registraJSON_A1_35(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA1_35 anacFormA1_35 = new AnacFormA1_35();

    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA1_35> listaAggiudicazioneA1_35 = new ArrayList<AggiudicazioneA1_35>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA1_35 aggiudicazioneA1_35 = new AggiudicazioneA1_35();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();

        if(codcig != null) {
          aggiudicazioneA1_35.setCig(codcig);
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          if(Long.valueOf(4).equals(esineg)) {
            esineg = Long.valueOf(6);
          } else {
            esineg = Long.valueOf(7);
          }
          aggiudicazioneA1_35.setEsitoProceduraAnnullata(new EnumNPA("esitoProceduraAnnullata", String.valueOf(esineg)));
          proceduraAnnullata = Boolean.TRUE;
        }

        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA1_35.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA1_35.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA1_35.setNumeroOfferteAmmesse(nofval);
        }
        
        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA1_35.setOffertePresentate(datiOfferte);
        }

        aggiudicazioneA1_35.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));

        aggiudicazioneA1_35.setnImpreseEscluseInsufficientiGiustificazioni(npaFunzioniManager.getNumeroImpreseEsclusePerInsufficientiGiustificazioni(codGar,ngara));

        listaAggiudicazioneA1_35.add(aggiudicazioneA1_35);
      }
      anacFormA1_35.setAggiudicazioni(listaAggiudicazioneA1_35);
    }


    String json = ow.writeValueAsString(anacFormA1_35);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_A2_32(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA2_32 anacFormA2_32 = new AnacFormA2_32();
    AppaltoA2_32 appaltoA2_32 = new AppaltoA2_32();
    appaltoA2_32.setModalitaRiaggiudicazioneAffidamento(null);
    appaltoA2_32.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));


    String getDatiTorn = "select t.motaggidir, t.ricastae, t.iterga "
        + "from torn t "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      String motaggidir = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 1);
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();


      DatiBaseProcedura baseProcedura = new DatiBaseProcedura();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));

      List<EnumNPA> giustificazioniAggiudicazioneDiretta = new ArrayList<EnumNPA>();
      if(motaggidir != null) {
        giustificazioniAggiudicazioneDiretta.add(new EnumNPA("giustificazioniAggiudicazioneDiretta", npaFunzioniManager.getTab2d1ByTab2tip(motaggidir, "A1z22")));
      }
      baseProcedura.setGiustificazioniAggiudicazioneDiretta(giustificazioniAggiudicazioneDiretta);

      appaltoA2_32.setDatiBaseProcedura(baseProcedura);

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProceduraOnlyAsta baseStrumentiProcedura = new DatiBaseStrumentiProceduraOnlyAsta(parameters);
      appaltoA2_32.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }
    anacFormA2_32.setAppalto(appaltoA2_32);


    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA2_32> listaAggiudicazioneA2_32 = new ArrayList<AggiudicazioneA2_32>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA2_32 aggiudicazioneA2_32 = new AggiudicazioneA2_32();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();
        
        if(codcig != null) {
          aggiudicazioneA2_32.setCig(codcig);
        }
        
        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA2_32.setOffertePresentate(datiOfferte);
        }

        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA2_32.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA2_32.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA2_32.setNumeroOfferteAmmesse(nofval);
        }

        // da aggiungere il controllo
        aggiudicazioneA2_32.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          aggiudicazioneA2_32.setnImpreseEscluseInsufficientiGiustificazioni(npaFunzioniManager.getNumeroImpreseEsclusePerInsufficientiGiustificazioni(codGar,ngara));

        aggiudicazioneA2_32.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if (dattoa != null) {
          aggiudicazioneA2_32.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneA2_32.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        listaAggiudicazioneA2_32.add(aggiudicazioneA2_32);
      }
      anacFormA2_32.setAggiudicazioni(listaAggiudicazioneA2_32);
    }


    String json = ow.writeValueAsString(anacFormA2_32);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }


  private void registraJSON_A2_35(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA2_35 anacFormA2_35 = new AnacFormA2_35();

    AppaltoA2_35 appaltoA2_35 = new AppaltoA2_35();
    appaltoA2_35.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));

    Vector<?> vetDatiTorn = this.sqlManager.getVector("select t.motaggidir, t.iterga from torn t where t.codgar = ? ", new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()) {
      String motaggidir = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();

      DatiBaseProcedura baseProcedura = new DatiBaseProcedura();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));

      List<EnumNPA> giustificazioniAggiudicazioneDiretta = new ArrayList<EnumNPA>();
      if (motaggidir != null) {
        giustificazioniAggiudicazioneDiretta.add(new EnumNPA("giustificazioniAggiudicazioneDiretta", npaFunzioniManager.getTab2d1ByTab2tip(motaggidir, "A1z22")));
      }
      baseProcedura.setGiustificazioniAggiudicazioneDiretta(giustificazioniAggiudicazioneDiretta);

      appaltoA2_35.setDatiBaseProcedura(baseProcedura);
    }
    anacFormA2_35.setAppalto(appaltoA2_35);


    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA2_35> listaAggiudicazioneA2_35 = new ArrayList<AggiudicazioneA2_35>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA2_35 aggiudicazioneA2_35 = new AggiudicazioneA2_35();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();

        if(codcig != null) {
          aggiudicazioneA2_35.setCig(codcig);
        }
        
        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA2_35.setOffertePresentate(datiOfferte);
        }

        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA2_35.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA2_35.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA2_35.setNumeroOfferteAmmesse(nofval);
        }

        aggiudicazioneA2_35.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
          aggiudicazioneA2_35.setnImpreseEscluseInsufficientiGiustificazioni(npaFunzioniManager.getNumeroImpreseEsclusePerInsufficientiGiustificazioni(codGar,ngara));


        aggiudicazioneA2_35.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if (dattoa != null) {
          aggiudicazioneA2_35.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneA2_35.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }


        listaAggiudicazioneA2_35.add(aggiudicazioneA2_35);
      }
      anacFormA2_35.setAggiudicazioni(listaAggiudicazioneA2_35);
    }


    String json = ow.writeValueAsString(anacFormA2_35);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_A4_5(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA4_5 anacFormA4_5 = new AnacFormA4_5();

    AppaltoA4_5 appaltoA4_5 = new AppaltoA4_5();
    String getDatiTorn = "select t.isprelprom, i.cfimp, i.nomest "
        + "from torn t "
        + "inner join impr i on t.codpromo = i.codimp "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Boolean isprelprom = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue());
      String cfein = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      String nomein = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();


      appaltoA4_5.setPrelazionePromotore(isprelprom);
      appaltoA4_5.setCodiceFiscalePromotore(cfein);
      appaltoA4_5.setDenominazionePromotore(nomein);
      appaltoA4_5.setModalitaRiaggiudicazioneAffidamento(null);
    }    
    anacFormA4_5.setAppalto(appaltoA4_5);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA4_5> listaAggiudicazioneA4_5 = new ArrayList<AggiudicazioneA4_5>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA4_5 aggiudicazioneA4_5 = new AggiudicazioneA4_5();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isconces = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 57).getValue());
        Double impcanoni = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 58).getValue();
        Double imputenza = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 59).getValue();
        Double impintro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 60).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();

        if(codcig != null) {
          aggiudicazioneA4_5.setCig(codcig);
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneA4_5.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        aggiudicazioneA4_5.setQuadroEconomicoConcessioni(new QuadroEconomicoConcessioni(tipgen, impapp, impsic, onprge, impnrl, impcanoni, imputenza, impintro, imprin, imppror, impaltro, impsomme, isconces));

        //Da ricontrollare il null
        aggiudicazioneA4_5.setDatiBaseRisultatoProcedura(new DatiBaseRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w"), null));

        if (dattoa != null) {
          aggiudicazioneA4_5.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        //Da controllare se e' corretto con Cristian
        aggiudicazioneA4_5.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(ngara))));

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA4_5.setOffertePresentate(datiOfferte);
        }

        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA4_5.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA4_5.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA4_5.setNumeroOfferteAmmesse(nofval);
        }

        listaAggiudicazioneA4_5.add(aggiudicazioneA4_5);
      }
      anacFormA4_5.setAggiudicazioni(listaAggiudicazioneA4_5);
    }

    String json = ow.writeValueAsString(anacFormA4_5);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_A1_33(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA1_33 anacFormA1_33 = new AnacFormA1_33();
    AppaltoA1_33 appaltoA1_33 = new AppaltoA1_33();
    List<String> listaCodiciLotto = new ArrayList<String>();
    Long tipgen = null;

    String getDatiTorn = "select moturg, sommaur, reluproc, tipgen "
        + "from torn "
        + "where codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      tipgen = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();


      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoA1_33.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoA1_33.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoA1_33.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoA1_33.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoA1_33.setModalitaRiaggiudicazioneAffidamento(null);

    }
    anacFormA1_33.setAppalto(appaltoA1_33);



    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA1_33> listaAggiudicazioneA1_33 = new ArrayList<AggiudicazioneA1_33>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA1_33 aggiudicazioneA1_33 = new AggiudicazioneA1_33();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Double limmax = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 6).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();


        listaCodiciLotto.add(ngara);

        if(codcig != null) {
          aggiudicazioneA1_33.setCig(codcig);
        }

        if(limmax != null) {
          //recupero il valore assoluto
          Double limmaxAssoluto = Math.abs(limmax);
          if(String.valueOf(limmaxAssoluto).matches(pattern_numerico)) {
            aggiudicazioneA1_33.setValoreSogliaAnomalia(limmaxAssoluto);
          }
        } else {
          //Errore pattern
        }

        aggiudicazioneA1_33.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          if(Long.valueOf(4).equals(esineg)) {
            esineg = Long.valueOf(6);
          } else {
            esineg = Long.valueOf(7);
          }
          aggiudicazioneA1_33.setEsitoProceduraAnnullata(new EnumNPA("esitoProceduraAnnullata", String.valueOf(esineg)));
          proceduraAnnullata = Boolean.TRUE;
        }
        
        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA1_33.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA1_33.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA1_33.setNumeroOfferteAmmesse(nofval);
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA1_33.setOffertePresentate(datiOfferte);
        }

        listaAggiudicazioneA1_33.add(aggiudicazioneA1_33);
      }
      anacFormA1_33.setAggiudicazioni(listaAggiudicazioneA1_33);
    }


    String json = ow.writeValueAsString(anacFormA1_33);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, listaCodiciLotto);

  }

  private void registraJSON_A2_33(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {
    AnacFormA2_33 anacFormA2_33 = new AnacFormA2_33();
    AppaltoA2_33 appaltoA2_33 = new AppaltoA2_33();
    List<String> listaCodiciLotto = new ArrayList<String>();
    Long tipgen = null;

    String getDatiTorn = "select moturg, sommaur, reluproc, motaggidir, tipgen, iterga "
        + "from torn "
        + "where codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Boolean reluproc = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue());
      String motaggidir = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      tipgen = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();


      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoA2_33.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoA2_33.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "A"));
      } else {
        appaltoA2_33.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoA2_33.setRelazioneUnicaSulleProcedure(reluproc==null?Boolean.valueOf(false):reluproc);

      appaltoA2_33.setModalitaRiaggiudicazioneAffidamento(null);

      appaltoA2_33.setDatiBaseSubappalti(new DatiBaseSubappalti(new EnumNPA("subappalto", npaFunzioniManager.getSubAppalto(codGar))));

      DatiBaseProcedura baseProcedura = new DatiBaseProcedura();
      baseProcedura.setTipoProcedura(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar));

      List<EnumNPA> giustificazioniAggiudicazioneDiretta = new ArrayList<EnumNPA>();
      if(motaggidir != null) {
        giustificazioniAggiudicazioneDiretta.add(new EnumNPA("giustificazioniAggiudicazioneDiretta", npaFunzioniManager.getTab2d1ByTab2tip(motaggidir, "A1z22")));
      }
      baseProcedura.setGiustificazioniAggiudicazioneDiretta(giustificazioniAggiudicazioneDiretta);

      appaltoA2_33.setDatiBaseProcedura(baseProcedura);


    }
    anacFormA2_33.setAppalto(appaltoA2_33);



    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, true);
    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<AggiudicazioneA2_33> listaAggiudicazioneA2_33 = new ArrayList<AggiudicazioneA2_33>();
      for (int i = 0; i < datiLotti.size(); i++) {
        AggiudicazioneA2_33 aggiudicazioneA2_33 = new AggiudicazioneA2_33();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        String codcig = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 5).getValue();
        Double limmax = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 6).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        String dattoa = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 21).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 47).getValue();
        Long nofval = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 17).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 56).getValue();
        Long esineg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 18).getValue();
        
        listaCodiciLotto.add(ngara);

        if(codcig != null) {
          aggiudicazioneA2_33.setCig(codcig);
        }

        if(limmax != null) {
          //recupero il valore assoluto
          Double limmaxAssoluto = Math.abs(limmax);
          if(String.valueOf(limmaxAssoluto).matches(pattern_numerico)) {
            aggiudicazioneA2_33.setValoreSogliaAnomalia(limmaxAssoluto);
          }
        } else {
          //Errore pattern
        }

        aggiudicazioneA2_33.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        aggiudicazioneA2_33.setDatiBaseRisultatoProcedura(new DatiBaseEsitoRisultatoProcedura(new EnumNPA("esitoProcedura", "selec-w")));

        if (dattoa != null) {
          aggiudicazioneA2_33.setDatiBaseAggiudicazioneAppalto(new DatiBaseAggiudicazioneAppalto(dattoa));
        }

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          aggiudicazioneA2_33.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        if(modlicg != null && !Long.valueOf(6).equals(modlicg)) {
          Double offertaMininmoRibasso = null;
          if (npaFunzioniManager.isApplicazioneVersioneAttiva("3"))
            offertaMininmoRibasso = npaFunzioniManager.getOffertaMinimoRibasso(codGar, ngara);
          aggiudicazioneA2_33.setDatiBaseSottomissioniRicevute(new DatiBaseSottomissioniRicevute(offertaMininmoRibasso, npaFunzioniManager.getOffertaMassimoRibasso(codGar, ngara)));
        }

        Boolean proceduraAnnullata = Boolean.FALSE;
        if(esineg != null) {
          proceduraAnnullata = Boolean.TRUE;
        }

        List<Offerte> datiOfferte = npaFunzioniManager.getListaOfferte(codGar, ngara, proceduraAnnullata);
        if(datiOfferte!=null && !datiOfferte.isEmpty()){
          aggiudicazioneA2_33.setOffertePresentate(datiOfferte);
        }

        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          aggiudicazioneA2_33.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else if(ditta != null && nofval == null) {
          aggiudicazioneA2_33.setNumeroOfferteAmmesse(npaFunzioniManager.getNumeroOfferteByDittaNgara(codGar, ngara, proceduraAnnullata));
        } else {
          aggiudicazioneA2_33.setNumeroOfferteAmmesse(nofval);
        }
        
        listaAggiudicazioneA2_33.add(aggiudicazioneA2_33);
      }
      anacFormA2_33.setAggiudicazioni(listaAggiudicazioneA2_33);
    }


    String json = ow.writeValueAsString(anacFormA2_33);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, listaCodiciLotto);

  }

  private void registraJSON_P1_12(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP1_12 anacFormP1_12 = new AnacFormP1_12();
    List<String> listaCodiciLotto = new ArrayList<String>();

    anacFormP1_12.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));

    AppaltoP_12 appaltoP_12 = new AppaltoP_12();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP_12.setCodiceAppalto(codiceAppaltoNpa);
    String getDatiTorn = "select terrid, sommaur, moturg, motcig, strumproc, idappaltonpa, idpianinpa from TORN where codgar = ? ";

    Vector<?> datiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(datiTorn!=null && !datiTorn.isEmpty()){
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue());
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue());
      Long moturg = (Long) SqlManager.getValueFromVectorParam(datiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(datiTorn, 3).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(datiTorn, 4).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 5).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(datiTorn, 6).getValue();

      
      appaltoP_12.setIdPianificazione(idpianinpa);
      appaltoP_12.setTermineRidottoAvvisoPreinformazione(terrid);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP_12.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP_12.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP_12.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP_12.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));        
      }
      if(strumproc != null) {
        appaltoP_12.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }
    }
    appaltoP_12.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    anacFormP1_12.setAppalto(appaltoP_12);


    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP_12> listalotti = new ArrayList<LottoP_12>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP_12 lotto = new LottoP_12();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();

        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));


        listaCodiciLotto.add(ngara);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        lotto.setCcnl(codcnel);
        
        lotto.setStrumentiElettroniciSpecifici(isstrumel);

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));


        listalotti.add(lotto);
      }
      anacFormP1_12.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_12);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, listaCodiciLotto);


  }

  private void registraJSON_P1_20(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP1_20 anacFormP1_20 = new AnacFormP1_20();

    anacFormP1_20.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP1_20 appaltoP1_20 = new AppaltoP1_20();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);

    appaltoP1_20.setCodiceAppalto(codiceAppaltoNpa);
    appaltoP1_20.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));


    String getDatiTorn = "select t.motcig, t.strumproc, t.idpianinpa, t.terrid, "
        + "t.sommaur, t.moturg "
        + "from torn t "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue());
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 5).getValue();



      if(motcig != null) {
        appaltoP1_20.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }

      if(strumproc != null) {
        appaltoP1_20.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP1_20.setIdPianificazione(idpianinpa);

      appaltoP1_20.setTermineRidottoAvvisoPreinformazione(terrid);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP1_20.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP1_20.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP1_20.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

    }


    anacFormP1_20.setAppalto(appaltoP1_20);



    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP1_20> listalotti = new ArrayList<LottoP1_20>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP1_20 lotto = new LottoP1_20();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        lotto.setStrumentiElettroniciSpecifici(isstrumel);
        
        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setCcnl(codcnel);

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));


        listalotti.add(lotto);
      }
      anacFormP1_20.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP1_20);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P2_12(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_12 anacFormP2_12 = new AnacFormP2_12();

    anacFormP2_12.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_12 appaltoP2_12 = new AppaltoP2_12();
    appaltoP2_12.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);
    appaltoP2_12.setCodiceAppalto(codiceAppaltoNpa);

    String gartel = null;

    String getDatiTorn = "select t.moturg, t.sommaur, t.strumproc, t.motcig, t.idappaltonpa, "
        + "t.accqua, t.ricastae, t.terrid, t.iterga, v.genere, t.destor, t.idpianinpa, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue());
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue();
      //String idappaltonpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 4).getValue();
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 5);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 6);
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue());
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 12).getValue();

      appaltoP2_12.setTermineRidottoAvvisoPreinformazione(terrid);
      appaltoP2_12.setIdPianificazione(idpianinpa);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP2_12.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP2_12.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP2_12.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }
      if(motcig != null) {
        appaltoP2_12.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }
      if(strumproc != null) {
        appaltoP2_12.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP2_12.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_12.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_12.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP2_12.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }


    anacFormP2_12.setAppalto(appaltoP2_12);



    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_12> listalotti = new ArrayList<LottoP2_12>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_12 lotto = new LottoP2_12();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        
        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setStrumentiElettroniciSpecifici(isstrumel);
        
        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }
        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setCui(codcui);
        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setCcnl(codcnel);

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));



        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvioSoloScadenza(npaFunzioniManager.unisciDataeOra(dtepar, otepar)));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        listalotti.add(lotto);
      }
      anacFormP2_12.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_12);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_P2_20(Long idscheda, String codGar, String codiceScheda, String tipoWSERP) throws Exception {

    AnacFormP2_20 anacFormP2_20 = new AnacFormP2_20();

    anacFormP2_20.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    AppaltoP2_20 appaltoP2_20 = new AppaltoP2_20();
    String codiceAppaltoNpa = valorizzaCodiceAppalto(idscheda, codGar);

    appaltoP2_20.setCodiceAppalto(codiceAppaltoNpa);
    appaltoP2_20.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByCodGarSQL(codGar, null));

    String gartel = null;

    String getDatiTorn = "select t.motcig, t.strumproc, t.idpianinpa, t.terrid, "
        + "t.accqua, t.ricastae, t.iterga, v.genere, t.destor, t.sommaur, t.moturg, t.gartel "
        + "from torn t "
        + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
        + "where t.codgar = ? ";
    Vector<?> vetDatiTorn = this.sqlManager.getVector(getDatiTorn, new Object[]{codGar});
    if(vetDatiTorn!=null && !vetDatiTorn.isEmpty()){
      Long motcig = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 0).getValue();
      Long strumproc = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 1).getValue();
      String idpianinpa = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 2).getValue();
      Boolean terrid = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 3).getValue());
      JdbcParametro accqua = SqlManager.getValueFromVectorParam(vetDatiTorn, 4);
      JdbcParametro ricastae = SqlManager.getValueFromVectorParam(vetDatiTorn, 5);
      Long iterga = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 6).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 7).getValue();
      String destor = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 8).getValue();
      Boolean sommaur = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(vetDatiTorn, 9).getValue());
      Long moturg = (Long) SqlManager.getValueFromVectorParam(vetDatiTorn, 10).getValue();
      gartel = (String) SqlManager.getValueFromVectorParam(vetDatiTorn, 11).getValue();


      if(motcig != null) {
        appaltoP2_20.setMotivazioneCIG(new EnumNPA("motivazioneCIG", String.valueOf(motcig)));
      }

      if(strumproc != null) {
        appaltoP2_20.setStrumentiSvolgimentoProcedure(new EnumNPA("strumentiSvolgimentoProcedure", String.valueOf(strumproc)));
      }

      appaltoP2_20.setIdPianificazione(idpianinpa);

      appaltoP2_20.setTermineRidottoAvvisoPreinformazione(terrid);

      if(sommaur != null && Boolean.TRUE.equals(sommaur) && moturg != null) {
        appaltoP2_20.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(moturg)));
        if(Long.valueOf(2).equals(moturg))
          appaltoP2_20.setLinkDocumenti(npaFunzioniManager.getUrlSQL(codGar, null, "P"));
      } else {
        appaltoP2_20.setMotivoUrgenza(new EnumNPA("motivoUrgenza", String.valueOf(7)));
      }

      appaltoP2_20.setDatiBaseProcedura(new DatiBaseProcedura2(npaFunzioniManager.getTipoProceduraByIterga(iterga, codGar)));

      if(genere != null) {
        if(Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
          appaltoP2_20.setDatiBase(new DatiBaseAppaltoOptional(destor, null, npaFunzioniManager.getValMax(codGar)));          
        }
        if(Long.valueOf(2).equals(genere)) {
          appaltoP2_20.setDatiBase(new DatiBaseAppaltoOptional(npaFunzioniManager.getNotGarByCodgarSql(codGar), null, npaFunzioniManager.getValMax(codGar)));          
        }
      }

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("npaFunzioniManager",npaFunzioniManager);
      parameters.put("ACCQUA",accqua);
      parameters.put("RICASTAE",ricastae);
      DatiBaseStrumentiProcedura baseStrumentiProcedura = new DatiBaseStrumentiProcedura(parameters);
      appaltoP2_20.setDatiBaseStrumentiProcedura(baseStrumentiProcedura);
    }


    anacFormP2_20.setAppalto(appaltoP2_20);



    //array lotti
    List<Vector> datiLotti = npaFunzioniManager.getDatiLottiSQL(idscheda, codGar, null);

    if(datiLotti!=null && !datiLotti.isEmpty()){
      List<LottoP2_20> listalotti = new ArrayList<LottoP2_20>();
      for (int i = 0; i < datiLotti.size(); i++) {
        LottoP2_20 lotto = new LottoP2_20();
        String codiga = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
        Double valmax = npaFunzioniManager.checkDoubleValue(SqlManager.getValueFromVectorParam(datiLotti.get(i), 48).getValue());
        Double imprin = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 15).getValue();
        Double impserv = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 16).getValue();
        Double impaltro = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 20).getValue();
        Boolean ispnrr = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 2).getValue());
        Long oggcontT = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 30).getValue();
        Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 4).getValue();
        String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 28).getValue();
        Boolean derogaaq = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 7).getValue());
        Long prevquota = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 8).getValue();
        Double quofemmi = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 9).getValue();
        Double quogiova = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 10).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 37).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 38).getValue();
        String affiris = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 40).getValue();
        Long critlicg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 42).getValue();
        Boolean costofisso = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 43).getValue());
        String accessi = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 44).getValue();
        Boolean isadesione = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 46).getValue());
        String codcnel = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 36).getValue();
        Long oggcontG = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 49).getValue();
        Long genere = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 50).getValue();
        String dtepar = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 41).getValue());
        String otepar = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 45).getValue();
        Long iterga = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 39).getValue();
        Boolean isnodpcm24 = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 53).getValue());
        Boolean isnoinizia = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 54).getValue());
        Boolean serviloc = npaFunzioniManager.getBooleanByStringNotNull((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 34).getValue());
        String codcui = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 23).getValue();
        Long dispopart = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 33).getValue();
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 3).getValue();
        Long tipneg = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue();
        String dteoff = npaFunzioniManager.getStringDateFormat((Date) SqlManager.getValueFromVectorParam(datiLotti.get(i), 51).getValue());
        String oteoff = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 52).getValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 11).getValue();
        Double impsic = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 12).getValue();
        Double onprge = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 13).getValue();
        Double impnrl = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 14).getValue();
        Double imppror = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 19).getValue();
        Double impsomme = (Double) SqlManager.getValueFromVectorParam(datiLotti.get(i), 27).getValue();
        Long derogasa = (Long) SqlManager.getValueFromVectorParam(datiLotti.get(i), 62).getValue();
        Boolean isstrumel = npaFunzioniManager.getBooleanByString((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 35).getValue());

        
        lotto.setDerogaQualificazione(NpaFunzioniManager.getDerogaQualificazione(derogasa));

        lotto.setStrumentiElettroniciSpecifici(isstrumel);
        
        lotto.setServizioPubblicoLocale(serviloc);

        lotto.setLotIdentifier(npaFunzioniManager.getLotIdentByCodiga(codiga));

        String locint = npaFunzioniManager.getCodIstat(codGar, ngara);
        if(locint != null) {
          if(Boolean.TRUE.equals(npaFunzioniManager.isApplicazioneVersioneAttiva("2"))){
            lotto.setCodIstat(new EnumNPA("codIstat", locint));
          } else {
            lotto.setCodIstat(new EnumNPA("codIstat", locint.replaceFirst("^0+(?!$)", "")));
          }
        }

        if(dispopart != null) {
          lotto.setContrattiDisposizioniParticolari(new EnumNPA("contrattiDisposizioniParticolari", String.valueOf(dispopart)));
        }

        lotto.setCategorieMerceologiche(npaFunzioniManager.getListaCategorieMerceologicheByNgaraSQL(ngara));

        if(NpaFunzioniManager.isTipnegDataFineNonNulla(tipneg)) {
          List<EnumNPA> condizioniNegoziata = new ArrayList<EnumNPA>();
          EnumNPA conEnumNPA = new EnumNPA();
          conEnumNPA.setIdTipologica("condizioniNegoziata");
          conEnumNPA.setCodice(String.valueOf(tipneg));
          condizioniNegoziata.add(conEnumNPA);
          lotto.setCondizioniNegoziata(condizioniNegoziata);
        }

        lotto.setAfferenteInvestimentiPNRR(ispnrr);

        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        lotto.setAcquisizioneCup(acquisizioneCup);
        lotto.setCupLotto(risultato.get(acquisizioneCup));

        Long oggcont = oggcontG;
        if(genere != null && Long.valueOf(300).equals(genere)) {
          oggcont = oggcontT;
        }

        if(oggcont != null) {
          lotto.setModalitaAcquisizione(npaFunzioniManager.getModalitaAcquisizioneByOggcont(oggcont));
        }

        lotto.setFinanziamenti(npaFunzioniManager.listaFinanziamentiByNgara(ngara));

        if(impserv != null && impserv > 0) {
          lotto.setRipetizioniEConsegneComplementari(Boolean.TRUE);
        } else {
          lotto.setRipetizioniEConsegneComplementari(Boolean.FALSE);
        }

        if (!npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
          lotto.setSaNonSoggettaObblighi24Dicembre2015(isnodpcm24);
          lotto.setIniziativeNonSoddisfacenti(isnoinizia);
        }

        lotto.setCui(codcui);

        if(codcui != null) {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.TRUE);
        } else {
          lotto.setLavoroOAcquistoPrevistoInProgrammazione(Boolean.FALSE);
        }

        lotto.setCcnl(codcnel);

        lotto.setOpzioniRinnovi(npaFunzioniManager.isOpzioniRinnoviMAGGIOREdiZero(imprin,imppror,impaltro));

        lotto.setCategoria(npaFunzioniManager.getCategoria(ngara, tipgen, codGar, genere));
        if(Long.valueOf(1).equals(tipgen)) {
          lotto.setCategoriaScorporabile(npaFunzioniManager.getCategorieScorporabili(ngara, tipgen, codGar, genere));
        }

        lotto.setParitaDiGenereGenerazionale(new ParitaDiGenereGenerazionale(derogaaq, prevquota, quofemmi, quogiova, 
            npaFunzioniManager.getListaMotivoDerogaByNgara(ngara), npaFunzioniManager.getListaMisurePremialiByNgara(ngara), ispnrr, isadesione, npaFunzioniManager.getIsUlterioriMisurePremiali(ngara)));

        lotto.setQuadroEconomicoStandard(new QuadroEconomico(tipgen, impapp, impsic, onprge, 
            impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda));

        lotto.setIpotesiCollegamento(npaFunzioniManager.getIpotesiCollegamentoByNgaraSQL(ngara));

        lotto.setDatiBase(new DatiBaseLottoOptional(npaFunzioniManager.getOggettoContrattoByTipgen(tipgen), not_gar, valmax));

        lotto.setDatiBaseContratto(npaFunzioniManager.getDatiBaseContratto(codnuts));

        lotto.setDatiBaseAggiuntivi(npaFunzioniManager.getDatiBaseAggiuntivi(affiris));

        lotto.setDatiBaseAggiudicazione(new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere));

        if(iterga != null) {
          if(!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff)));
          else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
            lotto.setDatiBaseTerminiInvio(new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null));
        }

        lotto.setDatiBaseCPV(npaFunzioniManager.getDatiBaseCpvByNgara(ngara));

        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          lotto.setDatiBaseAccessibilita(new DatiBaseAccessibilita(accessibilitaEnum, null));
        }

        lotto.setDatiBaseDocumenti(npaFunzioniManager.getDatiBaseDocumentiSQL(codGar, ngara, "P"));

        listalotti.add(lotto);
      }
      anacFormP2_20.setLotti(listalotti);
    }

    String json = ow.writeValueAsString(anacFormP2_20);
    //tracciaJson("AnacForm_"+codiceScheda+"_"+codGar+".json", json);

    npaFunzioniManager.insertJsonNpa(codiceAppaltoNpa, codGar, null, codiceScheda, json, null);

  }

  private void registraJSON_ANN(String codGar, String codiceScheda) throws Exception {
    AnacFormANN anacFormANN = new AnacFormANN();
    List<String> datiVgareGenere = npaFunzioniManager.getDatiVgareGenereSQL(codGar);

    if(datiVgareGenere!=null && !datiVgareGenere.isEmpty()) {
      String codiceLotto = (String) SqlManager.getValueFromVectorParam(datiVgareGenere.get(0), 0).getValue();

      anacFormANN.setCig(npaFunzioniManager.getCigDaGaraSQL(codiceLotto));
      Annullamento annullamento = new Annullamento();
      annullamento.setMotivoAnnullamento(new EnumNPA("motivoAnnullamento","1"));
      anacFormANN.setAnnullamento(annullamento);

      String json = ow.writeValueAsString(anacFormANN);

      npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);
    }
  }

  private void registraJSON_CM2(String codGar, String codiceScheda) throws Exception {
    AnacFormCM2 anacFormCM2 = new AnacFormCM2();
    List<String> datiVgareGenere = npaFunzioniManager.getDatiVgareGenereSQL(codGar);

    if(datiVgareGenere!=null && !datiVgareGenere.isEmpty()) {
      String codiceLotto = (String) SqlManager.getValueFromVectorParam(datiVgareGenere.get(0), 0).getValue();

      String nomeSchedaPrecedente = (String) sqlManager.getObject("select nome_scheda from g1npaform,g1cf_npa where g1npaform.idcfnpa=g1cf_npa.id and codgar=? and nome_scheda <> 'CM1' order by g1npaform.id desc", new Object[] {codGar});
      Double iaggiu = null;
      if ("CM1".equals(codiceScheda) && !"AD4".equals(nomeSchedaPrecedente)) {
        if ("P3_1".equals(nomeSchedaPrecedente)) {
          iaggiu = (Double) this.sqlManager.getObject("select valmax from g1schep31 where ngara = ?", new Object[]{codiceLotto});
        }
        if(iaggiu==null) {
          iaggiu = (Double) this.sqlManager.getObject("select valmax from v_gare_importi where ngara = ?", new Object[]{codiceLotto});
        }
      } else {
        iaggiu = (Double) this.sqlManager.getObject("select dq.iaggiu from ditgaq dq where dq.ngara=?", new Object[]{codiceLotto});
        if(iaggiu==null) {
          iaggiu = (Double) this.sqlManager.getObject("select g.iaggiu from gare g where g.ngara = ?", new Object[]{codiceLotto});
        }
      }

      Comunicazione comunicazione = null;
      String cup = (String) this.sqlManager.getObject("select cupprg from gare where ngara=?", new Object[]{codiceLotto});
      if (npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
        comunicazione = new Comunicazione<List<String>>();
        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cup, codiceLotto);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        comunicazione.setCup(risultato.get(acquisizioneCup));
      } else {
        comunicazione = new Comunicazione<String>();
        comunicazione.setCup(cup);
      }
      comunicazione.setMotivoRichiestaModifica(new EnumNPA("motivoRichiestaModifica","1"));
      comunicazione.setImporto(iaggiu);

      anacFormCM2.setComunicazione(comunicazione);
      anacFormCM2.setCig(npaFunzioniManager.getCigDaGaraSQL(codiceLotto));

      String json = ow.writeValueAsString(anacFormCM2);

      npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);
    }
  }

  private void registraJSON_PL1(String codGar, String codiceScheda) throws Exception {
    AnacFormPL1 anacForm = new AnacFormPL1();
    anacForm.setStazioniAppaltanti(npaFunzioniManager.getListaStazioniAppaltantiByCodGar(codGar));
    String json = ow.writeValueAsString(anacForm);
    npaFunzioniManager.insertJsonNpa(null, codGar, null, codiceScheda, json, null);
  }

  private void registraJSON_ID(Long idScheda, String codGar, String codiceScheda) throws Exception {
    HashMap<String, Object> parametri = new HashMap<>();
    parametri.put("IDSCHEDA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idScheda));
    parametri.put("npaFunzioniManager", npaFunzioniManager);
    parametri.put("sqlManager", sqlManager);

    String ngara = (String) sqlManager.getObject("select ngara from gare where ngara=(select ngara from g1npascheda where id=?)", new Object[]{idScheda});

    AnacFormID anacForm = new AnacFormID(parametri);
    String json = ow.writeValueAsString(anacForm);
    npaFunzioniManager.insertJsonNpa(null, codGar, ngara, codiceScheda, json, null);
  }

  private String valorizzaCodiceAppalto(Long idscheda, String codGar) throws SQLException {
    String codiceAppalto = (String) sqlManager.getObject("select codiceappalto from g1npascheda where id = (select idschedanparet from g1npascheda where id=?)", new Object[] {idscheda});
    if(StringUtils.isNotEmpty(codiceAppalto))
      return codiceAppalto;
    else {
      String uuid = UUID.randomUUID().toString();
      return npaFunzioniManager.togliDollaro(codGar)+"-"+uuid;
    }
  }
  
  private Object[] calcQuadroEconomicoConG1SCHEP31(Long tipgen, Double impapp, Double impsic, Double onprge, Double impnrl, 
      Double imprin, Double impserv, Double imppror, Double impaltro, Double impsomme, String codiceScheda, Double valmax, String ngara) throws SQLException {
    //ricalcolo il quadro economico tenendo conto dell'eventuale record in G1SCHEP31. ValMax serve per Dati Base (P3_1)
    Vector datiG1SCHEP31 = sqlManager.getVector("select impapp,impsic,impnrl,onprge,imprin,impserv,imppror,impaltro,valmax from g1schep31 where ngara=?", new Object[] {ngara});
    if(datiG1SCHEP31!=null && datiG1SCHEP31.size()>0) {
      Double g1schep31_impapp = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 0).getValue();
      Double g1schep31_impsic = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 1).getValue();
      Double g1schep31_impnrl = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 2).getValue();
      Double g1schep31_onprge = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 3).getValue();
      Double g1schep31_imprin = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 4).getValue();
      Double g1schep31_impserv = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 5).getValue();
      Double g1schep31_imppror = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 6).getValue();
      Double g1schep31_impaltro = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 7).getValue();
      Double g1schep31_valmax = (Double) SqlManager.getValueFromVectorParam(datiG1SCHEP31, 8).getValue();
      if(g1schep31_impapp!=null) impapp=g1schep31_impapp;
      if(g1schep31_impsic!=null) impsic=g1schep31_impsic;
      if(g1schep31_impnrl!=null) impnrl=g1schep31_impnrl;
      if(g1schep31_onprge!=null) onprge=g1schep31_onprge;
      if(g1schep31_imprin!=null) imprin=g1schep31_imprin;
      if(g1schep31_impserv!=null) impserv=g1schep31_impserv;
      if(g1schep31_imppror!=null) imppror=g1schep31_imppror;
      if(g1schep31_impaltro!=null) impaltro=g1schep31_impaltro;
      if(g1schep31_valmax!=null) valmax=g1schep31_valmax;
    }
    return new Object[]{new QuadroEconomico(tipgen, impapp, impsic, onprge, 
        impnrl, imprin, impserv, imppror, impaltro, impsomme, codiceScheda),valmax};
  }

}