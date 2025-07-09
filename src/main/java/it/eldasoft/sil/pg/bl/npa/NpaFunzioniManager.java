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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.sil.pg.bl.InviaVigilanzaManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.npa.model.*;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Classe di gestione delle funzionalita' per NPA
 *
 * @author Alex.Mancini
 */
public class NpaFunzioniManager {


  static Logger logger = Logger.getLogger(NpaFunzioniManager.class);

  private SqlManager sqlManager;
  private InviaVigilanzaManager inviaVigilanzaManager;
  private PgManagerEst1 pgManagerEst1;

  private static String QUERY_SELECT_CATEGORIA_PREVALENTE = "select cg.catiga,cs.tiplavg from catg cg, cais cs where cg.catiga = cs.caisim and cg.ncatg = 1 and cg.ngara = ?";
  private static String QUERY_COUNT_CATEGORIE_NGARA = "select count(*) from v_gare_categorie where ngara=?";
  private static String QUERY_SELECT_DATILOTTI =
          "select g.codiga, g.tipneg, t.ispnrr, g.cupprg, t.tipgen, g.codcig, g.limmax, "
          + "g1.derogaaq, g1.prevquota, g1.quofemmi, g1.quogiova, "
          + "g.impapp, g.impsic, g.onprge, g.impnrl, g1.imprin, g1.impserv, g.nofval, g.esineg, "
          + "g1.imppror, g1.impaltro, g.dattoa, g.locint, g1.codcui, g1.ammrin, g1.ammopz, g.tiplav, "
          + "g1.impsomme, g.ngara, t.accqua, t.oggcont, t.motesclu, g1.impproge, g1.dispopart, g1.serviloc, "
          + "g1.isstrumel, g1.codcnel, g.not_gar, t.codnuts, t.iterga, g1.affiris, t.dtepar, "
          + "g.critlicg, g1.costofisso, g1.accessi, t.otepar, t.isadesione, g.modlicg, vi.valmax, g.oggcont, "
          + "v.genere, t.dteoff, t.oteoff, t.isnodpcm24, t.isnoinizia, t.motesclc, g.ditta, t.isconces, "
          + "g1.impcanoni, g1.imputenza, g1.impintro, t.fattra, t.derogasa "
          + "from gare g "
          + "inner join gare1 g1 on g.ngara = g1.ngara "
          + "inner join torn t on t.codgar = g.codgar1 "
          + "inner join v_gare_importi vi on vi.codgar = t.codgar and vi.ngara = g1.ngara "
          + "inner join v_gare_genere v on v.codice = g.ngara ";
  private static String QUERY_SELECT_DATILOTTI_AVVISI =
          "select ga.oggetto as not_gar, ga.impavv as valmax, ga.ngara, t.ispnrr, t.accqua, t.oggcont, t.motesclu, t.codnuts, t.iterga," +
          " t.dtepar, t.otepar, t.isadesione, v.genere, t.dteoff, t.oteoff," +
          " t.isnodpcm24, t.isnoinizia, t.motesclc, t.isconces, t.fattra" +
          " from gareavvisi ga" +
          " inner join torn t on t.codgar = ga.codgar" +
          " inner join v_gare_genere v on v.codice = ga.ngara where t.codgar = ?";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setInviaVigilanzaManager(InviaVigilanzaManager inviaVigilanzaManager) {
    this.inviaVigilanzaManager = inviaVigilanzaManager;
  }
  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }
  
  public List<String> getDatiRagimpSQL(String codiceDitta) throws Exception {
    String getDatiRagimp = "select coddic, impman from ragimp where codime9 = ?";
    return this.sqlManager.getListVector(getDatiRagimp, new Object[]{codiceDitta});
  }
  
  public List<String> getDatiRagdetSQL(String codiceDitta, String codgar, String ngara, Long genere) throws Exception {
    if(genere != null && Long.valueOf(3).equals(genere)) {
      ngara = codgar;
    }
    String getDatiRagimp = "select coddic from ragdet where codimp = ? and ngara = ?";
    return this.sqlManager.getListVector(getDatiRagimp, new Object[]{codiceDitta, ngara});
  }

  public Vector<?> getDatiImprSQL(String codiceDitta) throws Exception {
    String getDatiImpr = "select tipimp, nomest, cfimp, nazimp from impr where codimp = ? ";
    return this.sqlManager.getVector(getDatiImpr, new Object[]{codiceDitta});
  }

  public List<String> getDatiVgareGenereSQL(String codGar) throws Exception {
    String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    String getDatiVgareGenere = "select codice "
                              + "from v_gare_genere v "
                              + "inner join gare g on v.codice = g.ngara "
                              + "where v.genere in (2,100,300) "
                              + "and g.codgar1 = ? ";
    
    if("ORA".equals(db)){
      getDatiVgareGenere += " order by LPAD(coalesce(g.codiga,'0'), 4, '0')";
    }else if("MSQ".equals(db)){
      getDatiVgareGenere += " order by RIGHT(REPLICATE('0', 4) + CONVERT(VARCHAR, coalesce(g.codiga,'0')), 4)";
    }else if("POS".equals(db)){
      getDatiVgareGenere += " order by LPAD(coalesce(g.codiga,'0'), 4, '0')";
    }
    return this.sqlManager.getListVector(getDatiVgareGenere, new Object[]{codGar});
  }

  public String getCigDaGaraSQL(String ngara) throws Exception {
    String getCig = "select codcig from gare where ngara = ? ";
    return (String) this.sqlManager.getObject(getCig, new Object[]{ngara});
  }

  public Boolean isDittaAggiudicatariaSQL(String dittao, String ngara) throws Exception {
    String getCig = "select count(*) from ditgaq where dittao = ? and ngara = ? ";
    Boolean idDittaAggiudicataria = Boolean.FALSE;
    Long count = (Long) this.sqlManager.getObject(getCig, new Object[]{dittao, ngara});
    if(count != null && count.longValue() > 0) {
      idDittaAggiudicataria = Boolean.TRUE;
    }
    return idDittaAggiudicataria;
  }

  public List<String> getAvvalimentoDitgAvvalSQL(String ngara, String codiceDitta) throws Exception {
    String getAvvalimentoDitgAvval = "select tipoav, dittaav, dittart "
        + "from DITGAVVAL "
        + "where ngara = ? "
        + "and dittao = ? "
        + "order by id desc";
    return this.sqlManager.getListVector(getAvvalimentoDitgAvval, new Object[]{ngara,codiceDitta});
  }

  public List<EnumNPA> getListaCategorieMerceologicheByCodGarSQL(String codGar, String ngara) throws Exception {
    List<String> lista = new ArrayList<String>();
    String getListaCategorieMerceologicheByCodGar = "select distinct catmerc "
        + "from gare1 "
        + "where codgar1 = ? ";
    if(ngara != null) {
      getListaCategorieMerceologicheByCodGar += " and ngara = ? ";
      lista = this.sqlManager.getListVector(getListaCategorieMerceologicheByCodGar, new Object[]{codGar, ngara});
    } else {
      lista = this.sqlManager.getListVector(getListaCategorieMerceologicheByCodGar, new Object[]{codGar});
    }
    List<EnumNPA> listaCategorieMerceologiche = null;
    if(lista != null && !lista.isEmpty()){
      listaCategorieMerceologiche = new ArrayList<EnumNPA>();
      for (int i = 0; i < lista.size(); i++) {

        Long codCatMerc = (Long) SqlManager.getValueFromVectorParam(lista.get(i), 0).getValue();
        if (codCatMerc != null) {
          EnumNPA enumNPACatMerc = new EnumNPA("categorieMerceologiche", String.valueOf(codCatMerc));
          listaCategorieMerceologiche.add(enumNPACatMerc);
        }
      }
    }
    return listaCategorieMerceologiche;
  }

  public List<EnumNPA> getListaCategorieMerceologicheByNgaraSQL(String ngara) throws Exception {
    String getListaCategorieMerceologicheByNgara = "select catmerc "
        + "from gare1 "
        + "where ngara = ? ";
    Long catmerc = (Long) this.sqlManager.getObject(getListaCategorieMerceologicheByNgara, new Object[]{ngara});
    List<EnumNPA> listaCategorieMerceologiche = null;
    if(catmerc!=null){
      listaCategorieMerceologiche = new ArrayList<EnumNPA>();
      EnumNPA enumNPACatMerc = new EnumNPA("categorieMerceologiche", String.valueOf(catmerc));
      listaCategorieMerceologiche.add(enumNPACatMerc);
    }
    return listaCategorieMerceologiche;
  }

  public IpotesiCollegamento getIpotesiCollegamentoByNgaraSQL(String ngara) throws Exception {
    IpotesiCollegamento ipotesiCollegamento = new IpotesiCollegamento();
    String getIpotesiCollegamentoByNgara = "select cigcoll, motcoll "
        + "from gare1 "
        + "where ngara = ? ";
    List<String> lista = this.sqlManager.getListVector(getIpotesiCollegamentoByNgara, new Object[]{ngara});
    List<String> listaCigcoll = null;
    if(lista!=null && !lista.isEmpty()){
      listaCigcoll = new ArrayList<String>();
      for (int i = 0; i < lista.size(); i++) {
        String cigcoll = (String) SqlManager.getValueFromVectorParam(lista.get(i), 0).getValue();
        Long motcoll = (Long) SqlManager.getValueFromVectorParam(lista.get(i), 1).getValue();
        if(cigcoll != null && !cigcoll.isEmpty()) {
          listaCigcoll.add(cigcoll);
        }
        if (motcoll != null) {
          ipotesiCollegamento.setMotivoCollegamento(new EnumNPA("motivoCollegamento", String.valueOf(motcoll)));
        }
      }
      if(!listaCigcoll.isEmpty())
        ipotesiCollegamento.setCigCollegato(listaCigcoll);
    }
    return ipotesiCollegamento;
  }
  
  public List<Partecipante> getListaPartecipanti(List<String> datiDitGPar, String codGar, String codiceLotto) throws Exception {
    List<Partecipante> listaPartecipanti = new ArrayList<Partecipante>();

    // Se gara e' a lotto unico
    if(Long.valueOf(3).equals(getGenereByCodgar(codGar))) {
      codiceLotto = codGar;
    }
    
    for (int y = 0; y < datiDitGPar.size(); y++) {
      String codiceDitta = (String) SqlManager.getValueFromVectorParam(datiDitGPar.get(y), 0).getValue();
      String idpartecipnpa = (String) SqlManager.getValueFromVectorParam(datiDitGPar.get(y), 1).getValue();
      Vector<?> datiImpr = getDatiImprSQL(codiceDitta);
      if(datiImpr!=null && !datiImpr.isEmpty()){
        Partecipante partecipante = new Partecipante();
        Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
        String nomest = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
        String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 2).getValue();
        Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 3).getValue();

        if(tipimp != null) {
          if(Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp)) {
            List<String> datiRagimp = getDatiRagimpSQL(codiceDitta);
            if(datiRagimp!=null && !datiRagimp.isEmpty()){
              for (int x = 0; x < datiRagimp.size(); x++) {
                partecipante = new Partecipante();
                String codiceDittaInt = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 0).getValue();
                String impMan = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 1).getValue();

                if(impMan != null && "1".equals(impMan))
                  partecipante.setRuoloOE(new EnumNPA("ruoloOE", "1"));
                else
                  partecipante.setRuoloOE(new EnumNPA("ruoloOE", "2"));

                Vector<?> datiImpreseDaRTI = getDatiImprSQL(codiceDittaInt);
                if(datiImpreseDaRTI!=null && !datiImpreseDaRTI.isEmpty()){
                  tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 0).getValue();
                  nomest = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 1).getValue();
                  cfimp = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 2).getValue();
                  nazimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 3).getValue();
                  partecipante.setTipoOE(calcolaTipoOE(tipimp));
                  partecipante.setCodiceFiscale(cfimp);
                  partecipante.setDenominazione(nomest);
                  partecipante.setIdPartecipante(idpartecipnpa);
                }

                if(nazimp != null) {
                  partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
                }
                partecipante.setAvvalimento(false);
                List<String> datiDitgAvval = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
                if(datiDitgAvval!=null && !datiDitgAvval.isEmpty()){
                  for (int j = 0; j < datiDitgAvval.size(); j++) {
                    Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 0).getValue();
                    String dittart = (String) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 2).getValue();
                    if((dittart == null || (dittart != null && dittart.equals(codiceDittaInt))) && tipoav != null) {
                      partecipante.setAvvalimento(true);
                      partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
                    }
                  }
                }
                listaPartecipanti.add(partecipante);
              }
            }
          } else if(Boolean.TRUE.equals(isApplicazioneVersioneAttiva("2")) && (Long.valueOf(2).equals(tipimp) || Long.valueOf(11).equals(tipimp))){
            partecipante.setCodiceFiscale(cfimp);
            partecipante.setDenominazione(nomest);
            partecipante.setRuoloOE(new EnumNPA("ruoloOE", "7"));
            partecipante.setTipoOE(calcolaTipoOE(tipimp));
            partecipante.setIdPartecipante(idpartecipnpa);

            if(nazimp != null) {
              partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
            }

            partecipante.setAvvalimento(false);
            List<String> datiDitgAvval = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
            if(datiDitgAvval!=null && !datiDitgAvval.isEmpty()){
              partecipante.setAvvalimento(true);
              for (int j = 0; j < datiDitgAvval.size(); j++) {
                Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 0).getValue();
                if(tipoav != null) {
                  partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
                }
              }
            }
            listaPartecipanti.add(partecipante);
            
            //
            List<String> datiRagdet = getDatiRagdetSQL(codiceDitta, codGar, codiceLotto, getGenereByCodgar(codGar));
            if(datiRagdet!=null && !datiRagdet.isEmpty()){
              for (int x = 0; x < datiRagdet.size(); x++) {
                partecipante = new Partecipante();
                String codiceDittaInt = (String) SqlManager.getValueFromVectorParam(datiRagdet.get(x), 0).getValue();
                
                partecipante.setRuoloOE(new EnumNPA("ruoloOE", "11"));

                Vector<?> datiImpreseDaConsorzio = getDatiImprSQL(codiceDittaInt);
                if(datiImpreseDaConsorzio!=null && !datiImpreseDaConsorzio.isEmpty()){
                  tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaConsorzio, 0).getValue();
                  nomest = (String) SqlManager.getValueFromVectorParam(datiImpreseDaConsorzio, 1).getValue();
                  cfimp = (String) SqlManager.getValueFromVectorParam(datiImpreseDaConsorzio, 2).getValue();
                  nazimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaConsorzio, 3).getValue();
                  partecipante.setTipoOE(calcolaTipoOE(tipimp));
                  partecipante.setCodiceFiscale(cfimp);
                  partecipante.setDenominazione(nomest);
                  partecipante.setIdPartecipante(idpartecipnpa);
                }

                if(nazimp != null) {
                  partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
                }
                partecipante.setAvvalimento(false);
                List<String> datiDitgAvval1 = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
                if(datiDitgAvval1!=null && !datiDitgAvval1.isEmpty()){
                  for (int j = 0; j < datiDitgAvval1.size(); j++) {
                    Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval1.get(j), 0).getValue();
                    String dittart = (String) SqlManager.getValueFromVectorParam(datiDitgAvval1.get(j), 2).getValue();
                    if((dittart == null || (dittart != null && dittart.equals(codiceDittaInt))) && tipoav != null) {
                      partecipante.setAvvalimento(true);
                      partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
                    }
                  }
                }
                listaPartecipanti.add(partecipante);
              }
            }
          } else {
            partecipante.setCodiceFiscale(cfimp);
            partecipante.setDenominazione(nomest);
            if(Boolean.TRUE.equals(isApplicazioneVersioneAttiva("2"))){
              partecipante.setRuoloOE(new EnumNPA("ruoloOE", "3"));
            } else {
              partecipante.setRuoloOE(new EnumNPA("ruoloOE", "1"));
            }
            partecipante.setTipoOE(calcolaTipoOE(tipimp));
            partecipante.setIdPartecipante(idpartecipnpa);

            if(nazimp != null) {
              partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
            }

            partecipante.setAvvalimento(false);
            List<String> datiDitgAvval = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
            if(datiDitgAvval!=null && !datiDitgAvval.isEmpty()){
              partecipante.setAvvalimento(true);
              for (int j = 0; j < datiDitgAvval.size(); j++) {
                Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 0).getValue();
                if(tipoav != null) {
                  partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
                }
              }
            }
            listaPartecipanti.add(partecipante);
          }
        } else {
          partecipante.setCodiceFiscale(cfimp);
          partecipante.setDenominazione(nomest);
          if(Boolean.TRUE.equals(isApplicazioneVersioneAttiva("2"))){
            partecipante.setRuoloOE(new EnumNPA("ruoloOE", "3"));
          } else {
            partecipante.setRuoloOE(new EnumNPA("ruoloOE", "1"));
          }
          partecipante.setTipoOE(calcolaTipoOE(tipimp));
          partecipante.setIdPartecipante(idpartecipnpa);

          if(nazimp != null) {
            partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
          }

          partecipante.setAvvalimento(false);
          List<String> datiDitgAvval = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
          if(datiDitgAvval!=null && !datiDitgAvval.isEmpty()){
            partecipante.setAvvalimento(true);
            for (int j = 0; j < datiDitgAvval.size(); j++) {
              Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 0).getValue();
              if(tipoav != null) {
                partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
              }
            }
          }
          listaPartecipanti.add(partecipante);
        
        }
        //APPALTI-1821 aggiungo ditte ausiliarie (avvalimento)
        aggiungiDitteAvvalimento(listaPartecipanti,idpartecipnpa,codiceLotto,codiceDitta);
      }
    }
    
    return listaPartecipanti;
  
  }


  public List<PartecipanteAD> getListaPartecipantiAD(List<String> datiDitta, String codiceLotto, boolean isSchedaAD, boolean isA7_1) throws Exception {
    List<PartecipanteAD> listaPartecipanti = new ArrayList<PartecipanteAD>();

    for (int y = 0; y < datiDitta.size(); y++) {
      String codiceDitta = (String) SqlManager.getValueFromVectorParam(datiDitta.get(y), 0).getValue();
      Double iaggiu = (Double) SqlManager.getValueFromVectorParam(datiDitta.get(y), 1).getValue();
      String idpartecipnpa = (String) SqlManager.getValueFromVectorParam(datiDitta.get(y), 2).getValue();
      Vector<?> datiImpr = getDatiImprSQL(codiceDitta);
      if(datiImpr!=null && !datiImpr.isEmpty()){
        PartecipanteAD partecipante = new PartecipanteAD();
        Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
        String nomest = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
        String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 2).getValue();
        Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 3).getValue();

        if(tipimp != null) {
          if(Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp)) {
            List<String> datiRagimp = getDatiRagimpSQL(codiceDitta);
            if(datiRagimp!=null && !datiRagimp.isEmpty()){
              for (int x = 0; x < datiRagimp.size(); x++) {
                partecipante = new PartecipanteAD();
                String codiceDittaInt = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 0).getValue();
                String impMan = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 1).getValue();
                partecipante.setImporto(iaggiu);
                if(impMan != null && "1".equals(impMan))
                  partecipante.setRuoloOE(new EnumNPA("ruoloOE", "1"));
                else {
                  if (isA7_1) { //APP-1956 per A7_1_1 e A7_1_2 prendo solo la mandataria
                    continue;
                  }
                  partecipante.setRuoloOE(new EnumNPA("ruoloOE", "2"));
                }

                Vector<?> datiImpreseDaRTI = getDatiImprSQL(codiceDittaInt);
                if(datiImpreseDaRTI!=null && !datiImpreseDaRTI.isEmpty()){
                  tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 0).getValue();
                  nomest = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 1).getValue();
                  cfimp = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 2).getValue();
                  nazimp = (Long) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 3).getValue();
                  partecipante.setTipoOE(calcolaTipoOE(tipimp));
                  partecipante.setCodiceFiscale(cfimp);
                  partecipante.setDenominazione(nomest);
                  partecipante.setIdPartecipante(idpartecipnpa);
                }

                if(nazimp != null) {
                  partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
                }

                partecipante.setAvvalimento(false);
                List<String> datiDitgAvval = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
                if(datiDitgAvval!=null && !datiDitgAvval.isEmpty()){
                  for (int j = 0; j < datiDitgAvval.size(); j++) {
                    Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 0).getValue();
                    String dittart = (String) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 2).getValue();
                    if((dittart == null || (dittart != null && dittart.equals(codiceDittaInt))) && tipoav != null) {
                      partecipante.setAvvalimento(true);
                      partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
                    }
                  }
                }
                listaPartecipanti.add(partecipante);
              }
            }
          } else if(Boolean.TRUE.equals(isApplicazioneVersioneAttiva("2")) && (Long.valueOf(2).equals(tipimp) || Long.valueOf(11).equals(tipimp))){
            partecipante.setCodiceFiscale(cfimp);
            partecipante.setDenominazione(nomest);
            partecipante.setRuoloOE(new EnumNPA("ruoloOE", "7"));
            partecipante.setTipoOE(calcolaTipoOE(tipimp));
            partecipante.setImporto(iaggiu);
            partecipante.setIdPartecipante(idpartecipnpa);

            if(nazimp != null) {
              partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
            }

            partecipante.setAvvalimento(false);
            List<String> datiDitgAvval = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
            if(datiDitgAvval!=null && !datiDitgAvval.isEmpty()){
              partecipante.setAvvalimento(true);
              for (int j = 0; j < datiDitgAvval.size(); j++) {
                Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 0).getValue();
                if(tipoav != null) {
                  partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
                }
              }
            }
            listaPartecipanti.add(partecipante);
            //APPALTI-1911
            if(isSchedaAD)
              aggiungiConsorziateEsecutrici(listaPartecipanti,idpartecipnpa,codiceLotto,codiceDitta,iaggiu);
          
          } else {
            partecipante.setCodiceFiscale(cfimp);
            partecipante.setDenominazione(nomest);
            if(Boolean.TRUE.equals(isApplicazioneVersioneAttiva("2"))){
              partecipante.setRuoloOE(new EnumNPA("ruoloOE", "3"));
            } else {
              partecipante.setRuoloOE(new EnumNPA("ruoloOE", "1"));
            }
            partecipante.setTipoOE(calcolaTipoOE(tipimp));
            partecipante.setImporto(iaggiu);
            partecipante.setIdPartecipante(idpartecipnpa);

            if(nazimp != null) {
              partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
            }

            partecipante.setAvvalimento(false);
            List<String> datiDitgAvval = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
            if(datiDitgAvval!=null && !datiDitgAvval.isEmpty()){
              partecipante.setAvvalimento(true);
              for (int j = 0; j < datiDitgAvval.size(); j++) {
                Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 0).getValue();
                if(tipoav != null) {
                  partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
                }
              }
            }
            listaPartecipanti.add(partecipante);
          }
        } else {
          partecipante.setCodiceFiscale(cfimp);
          partecipante.setDenominazione(nomest);
          if(Boolean.TRUE.equals(isApplicazioneVersioneAttiva("2"))){
            partecipante.setRuoloOE(new EnumNPA("ruoloOE", "3"));
          } else {
            partecipante.setRuoloOE(new EnumNPA("ruoloOE", "1"));
          }
          partecipante.setTipoOE(calcolaTipoOE(tipimp));
          partecipante.setImporto(iaggiu);
          partecipante.setIdPartecipante(idpartecipnpa);

          if(nazimp != null) {
            partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
          }

          partecipante.setAvvalimento(false);
          List<String> datiDitgAvval = getAvvalimentoDitgAvvalSQL(codiceLotto, codiceDitta);
          if(datiDitgAvval!=null && !datiDitgAvval.isEmpty()){
            partecipante.setAvvalimento(true);
            for (int j = 0; j < datiDitgAvval.size(); j++) {
              Long tipoav = (Long) SqlManager.getValueFromVectorParam(datiDitgAvval.get(j), 0).getValue();
              if(tipoav != null) {
                partecipante.setTipologiaAvvalimento(getTipologiaAvvaliamentoByTipoAv(tipoav));
              }
            }
          }
          listaPartecipanti.add(partecipante);
        }
        if(isSchedaAD)//APPALTI-1911
          aggiungiDitteAvvalimento(listaPartecipanti,idpartecipnpa,codiceLotto,codiceDitta,iaggiu);
      }
    }

    return listaPartecipanti;
  
  }
  
  public List<AggiudicatarioAD5> getListaPartecipantiAD5(List<String> datiDitta, String codiceLotto) throws Exception {
    List<AggiudicatarioAD5> listaPartecipanti = new ArrayList<AggiudicatarioAD5>();

    for (int y = 0; y < datiDitta.size(); y++) {
      AggiudicatarioAD5 aggiudicatarioAD5 = new AggiudicatarioAD5();
      String codiceDitta = (String) SqlManager.getValueFromVectorParam(datiDitta.get(y), 0).getValue();
      Double iaggiu = (Double) SqlManager.getValueFromVectorParam(datiDitta.get(y), 1).getValue();
      String idpartecipnpa = (String) SqlManager.getValueFromVectorParam(datiDitta.get(y), 2).getValue();

      aggiudicatarioAD5.setIdPartecipante(idpartecipnpa);
      aggiudicatarioAD5.setImporto(iaggiu);

      Vector<?> datiImpr = getDatiImprSQL(codiceDitta);
      if(datiImpr!=null && !datiImpr.isEmpty()){
        Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
        String nomest = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
        String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 2).getValue();

        aggiudicatarioAD5.setDenominazione(nomest);
        aggiudicatarioAD5.setCodiceFiscale(cfimp);

        if(tipimp != null && (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp))) {
          List<String> datiRagimp = getDatiRagimpSQL(codiceDitta);
          if(datiRagimp!=null && !datiRagimp.isEmpty()){
            for (int x = 0; x < datiRagimp.size(); x++) {
              codiceDitta = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 0).getValue();
              String impMan = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 1).getValue();
              if(impMan != null && "1".equals(impMan)) {
                Vector<?> datiImpreseDaRTI = getDatiImprSQL(codiceDitta);
                if(datiImpreseDaRTI!=null && !datiImpreseDaRTI.isEmpty()){
                  cfimp = (String) SqlManager.getValueFromVectorParam(datiImpreseDaRTI, 2).getValue();
                  aggiudicatarioAD5.setCodiceFiscale(cfimp);
                }
              }
            }
          }
        }
      }
      listaPartecipanti.add(aggiudicatarioAD5);
    }
    return listaPartecipanti;
  
  }
  
  
  public List<PartecipanteAD> getListaPartecipADDiscrAQ(String codiceLotto) throws Exception {
    List<PartecipanteAD> listaPartecipanti = null;
    String getDittaPartecipanti = null;
    List<String> datiDitta = null;
    getDittaPartecipanti = "select dq.dittao, dq.iaggiu, d.idpartecipnpa "
          + "from ditgaq dq inner join ditg d "
          + "on d.ngara5 = dq.ngara and d.dittao = dq.dittao "
          + "where dq.ngara=?";
    datiDitta = this.sqlManager.getListVector(getDittaPartecipanti, new Object[]{codiceLotto});
    if(datiDitta!=null && !datiDitta.isEmpty()){
      listaPartecipanti = getListaPartecipantiAD(datiDitta, codiceLotto,true, false);
    } else {
      getDittaPartecipanti = "select g.ditta, g.iaggiu, d.idpartecipnpa "
          + "from gare g inner join ditg d "
          + "on d.codgar5 = g.codgar1 and d.ngara5 = g.ngara and d.dittao = g.ditta "
          + "where g.ngara = ?";
      datiDitta = this.sqlManager.getListVector(getDittaPartecipanti, new Object[]{codiceLotto});
      if(datiDitta!=null && !datiDitta.isEmpty()){
        listaPartecipanti = getListaPartecipantiAD(datiDitta, codiceLotto,true, false);
      }
    }
    return listaPartecipanti;
  }
  
  public List<AggiudicatarioAD5> getListaPartecipantiAD5DiscrAQ(String codiceLotto) throws Exception {
    List<AggiudicatarioAD5> listaAggiudicatari = null;
    String getDitteAggiudicatarie = null;
    List<String> datiDitte = null;
    getDitteAggiudicatarie = "select dq.dittao, dq.iaggiu, d.idpartecipnpa "
          + "from ditgaq dq inner join ditg d "
          + "on d.ngara5 = dq.ngara and d.dittao = dq.dittao "
          + "where dq.ngara=?";
    datiDitte = this.sqlManager.getListVector(getDitteAggiudicatarie, new Object[]{codiceLotto});
    if(datiDitte!=null && !datiDitte.isEmpty()){
      listaAggiudicatari = getListaPartecipantiAD5(datiDitte, codiceLotto);
    } else {
      getDitteAggiudicatarie = "select g.ditta, g.iaggiu, d.idpartecipnpa "
          + "from gare g inner join ditg d "
          + "on d.codgar5 = g.codgar1 and d.ngara5 = g.ngara and d.dittao = g.ditta "
          + "where g.ngara = ?";
      datiDitte = this.sqlManager.getListVector(getDitteAggiudicatarie, new Object[]{codiceLotto});
      if(datiDitte!=null && !datiDitte.isEmpty()){
        listaAggiudicatari = getListaPartecipantiAD5(datiDitte, codiceLotto);
      }
    }
    return listaAggiudicatari;
  }

  /**
   * @param codGar
   * @param ulterioreFiltro
   * @return 
   * @throws Exception
   * posizione 0 = codiga
   * posizione 1 = tipneg
   * posizione 2 = ispnrr
   * posizione 3 = cupprg
   * posizione 4 = tipgen
   * posizione 5 = codcig
   * posizione 6 = limmax
   * posizione 7 = derogaaq
   * posizione 8 = prevquota
   * posizione 9 = quofemmi
   * posizione 10 = quogiova
   * posizione 11 = impapp
   * posizione 12 = impsic
   * posizione 13 = onprge
   * posizione 14 = impnrl
   * posizione 15 = imprin
   * posizione 16 = impserv
   * posizione 17 = nofval
   * posizione 18 = esineg
   * posizione 19 = imppror
   * posizione 20 = impaltro
   * posizione 21 = dattoa
   * posizione 22 = locint
   * posizione 23 = codcui
   * posizione 24 = ammrin
   * posizione 25 = ammopz
   * posizione 26 = tiplav
   * posizione 27 = impsomme
   * posizione 28 = ngara
   * posizione 29 = accqua
   * posizione 30 = oggcont torn
   * posizione 31 = motesclu
   * posizione 32 = impproge
   * posizione 33 = dispopart
   * posizione 34 = serviloc
   * posizione 35 = isstrumel
   * posizione 36 = codcnel
   * posizione 37 = not_gar
   * posizione 38 = codnuts
   * posizione 39 = iterga
   * posizione 40 = affiris
   * posizione 41 = dtepar
   * posizione 42 = critlicg
   * posizione 43 = costofisso
   * posizione 44 = accessi
   * posizione 45 = otepar
   * posizione 46 = isadesione
   * posizione 47 = modlicg
   * posizione 48 = valmax
   * posizione 49 = oggcont gare
   * posizione 50 = genere
   * posizione 51 = dteoff
   * posizione 52 = oteoff
   * posizione 53 = isnodpcm24
   * posizione 54 = isnoinizia
   * posizione 55 = motesclc
   * posizione 56 = ditta
   * posizione 57 = isconces
   * posizione 58 = impcanoni
   * posizione 59 = imputenza
   * posizione 60 = impintro
   * posizione 61 = fattra
   * posizione 62 = derogasa
   */
  public List<Vector> getDatiLottiSQL(Long idscheda, String codGar, Boolean isSchedaA) throws Exception {
    String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    List<Vector> list = null;
    String getDatiLotti = QUERY_SELECT_DATILOTTI;
    if(Boolean.TRUE.equals(isSchedaA)) {
      getDatiLotti += "inner join g1npalottischeda ls on ls.ngara = g1.ngara and ls.idscheda = ? ";
    }
    getDatiLotti += "where v.genere in (2,100,300) "
        + "and t.codgar = ? ";
    
    if("ORA".equals(db)){
      getDatiLotti += "order by LPAD(coalesce(g.codiga,'0'), 4, '0')";
    }else if("MSQ".equals(db)){
      getDatiLotti += "order by RIGHT(REPLICATE('0', 4) + CONVERT(VARCHAR, coalesce(g.codiga,'0')), 4)";
    }else if("POS".equals(db)){
      getDatiLotti += "order by LPAD(coalesce(g.codiga,'0'), 4, '0')";
    }
    
    if(Boolean.TRUE.equals(isSchedaA)) {
      list = this.sqlManager.getListVector(getDatiLotti, new Object[]{idscheda,codGar});
    }else {
      list = this.sqlManager.getListVector(getDatiLotti, new Object[]{codGar});	
    }
    
    Vector datiG1nparet = sqlManager.getVector("select dtepar, otepar, dteoff, oteoff from G1NPARET where idnpascheda=?", new Object[]{idscheda});
    if(datiG1nparet!=null && datiG1nparet.size()>0) {//sostituzione delle occorrenze relative ai termini per i dati dei lotti
      for(int i=0; i<list.size();i++) {
        Vector v = list.get(i);
        v.set(41, datiG1nparet.get(0));
        v.set(45, datiG1nparet.get(1));
        v.set(51, datiG1nparet.get(2));
        v.set(52, datiG1nparet.get(3));
        list.set(i, v);
      }
    }
    return list;
  }

  public List<Map> getMappaDatiLottiSQL(Long idscheda, String codGar, Boolean isSchedaA, Long genereGara) throws Exception {
    String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    List<Map> list = null;
    String getDatiLotti;
    if (((Long)11L).equals(genereGara)) {
      getDatiLotti = QUERY_SELECT_DATILOTTI_AVVISI;
    } else {
      getDatiLotti = QUERY_SELECT_DATILOTTI;
      if(Boolean.TRUE.equals(isSchedaA)) {
        getDatiLotti += "inner join g1npalottischeda ls on ls.ngara = g1.ngara and ls.idscheda = ? ";
      }
      getDatiLotti += "where v.genere in (2,100,300) "
              + "and t.codgar = ? ";

      if("ORA".equals(db)){
        getDatiLotti += "order by LPAD(coalesce(g.codiga,'0'), 4, '0')";
      }else if("MSQ".equals(db)){
        getDatiLotti += "order by RIGHT(REPLICATE('0', 4) + CONVERT(VARCHAR, coalesce(g.codiga,'0')), 4)";
      }else if("POS".equals(db)){
        getDatiLotti += "order by LPAD(coalesce(g.codiga,'0'), 4, '0')";
      }

    }

    if(Boolean.TRUE.equals(isSchedaA)) {
      list = this.sqlManager.getListHashMap(getDatiLotti, new Object[]{idscheda,codGar});
    }else {
      list = this.sqlManager.getListHashMap(getDatiLotti, new Object[]{codGar});
    }

    Map datiG1nparet = sqlManager.getHashMap("select dtepar, otepar, dteoff, oteoff from G1NPARET where idnpascheda=?", new Object[]{idscheda});
    if(datiG1nparet!=null && datiG1nparet.size()>0) {//sostituzione delle occorrenze relative ai termini per i dati dei lotti
      for(int i=0; i<list.size();i++) {
        Map h = list.get(i);
        h.putAll(datiG1nparet);
        list.set(i, h);
      }
    }
    return list;
  }

  public EnumNPA calcolaTipoOE(Long tipimp) {
    EnumNPA tipoOEEnum = null;
    String tipoOE = null;
    switch (String.valueOf(tipimp)) {
    case "1":
    case "5":
    case "6":
    case "7":
    case "8":
    case "9":
    case "12":
    case "13":
      tipoOE = "1";
      break;
    case "2":
    case "11":
      tipoOE = "3";
      break;
    case "3":
    case "10":
      tipoOE = "4";
      break;
    case "14":
      tipoOE = "6";
      break;
    case "4":
      tipoOE = "7";
      break;
      /*
    case "non disponibile":
      tipoOE = "2";
      break;
       */
    default:
      break;
    }
    if(tipoOE != null) {
      tipoOEEnum = new EnumNPA("tipoOE", tipoOE);
    }

    return tipoOEEnum;
  }

  public String getLotIdentByCodiga(String codiga) {
    String lotIdentifier = null;
    if(codiga != null && !"".equals(codiga)) {
      lotIdentifier = "LOT-"+String.format("%0" + 4 + "d", Integer.valueOf(codiga));
    } else {
      lotIdentifier = "LOT-"+String.format("%0" + 4 + "d", Integer.valueOf(1));
    }
    return lotIdentifier;
  }

/*
  public String getIdPianificazioneByDescSQL(String codiceScheda, String codGar) throws Exception {
    String getIdSchedaPreinformazione = "select c.idschedanpa "
        + "from g1cf_npa a "
        + "inner join g1npaform b on a.id = b.idcfnpa "
        + "inner join g1npascheda c on b.id = c.idform "
        + "where a.nome_scheda = ? "
        + "and b.codgar = ? ";
    return (String) this.sqlManager.getObject(getIdSchedaPreinformazione, new Object[]{codiceScheda, codGar});
  }*/

  public List<StazioneAppaltante> getListaStazioniAppaltantiByCodGar(String codGar) throws SQLException {
    List<StazioneAppaltante> listaStazioniAppaltanti = null;
    String getDatiUffint= "select u.cfein, u.codausa, t.codcentro, t.funzsvolte, t.altrisog, u.cfanac, u.codausaanac, t.cenint "
        + "from torn t "
        + "inner join UFFINT u on t.cenint = u.codein "
        + "where t.codgar = ? ";
    Vector<?> datiUffint = this.sqlManager.getVector(getDatiUffint, new Object[]{codGar});

    if(datiUffint!=null && !datiUffint.isEmpty()){
      listaStazioniAppaltanti = new ArrayList<StazioneAppaltante>();
      StazioneAppaltante stazioneAppaltante = new StazioneAppaltante();

      String cfein = (String) SqlManager.getValueFromVectorParam(datiUffint, 0).getValue();
      String codausa = (String) SqlManager.getValueFromVectorParam(datiUffint, 1).getValue();
      String codcentro = (String) SqlManager.getValueFromVectorParam(datiUffint, 2).getValue();
      Long funzsvolte = (Long) SqlManager.getValueFromVectorParam(datiUffint, 3).getValue();
      Long altrisog = (Long) SqlManager.getValueFromVectorParam(datiUffint, 4).getValue();
      String cfanac = (String) SqlManager.getValueFromVectorParam(datiUffint, 5).getValue();
      String codausaanac = (String) SqlManager.getValueFromVectorParam(datiUffint, 6).getValue();
      String cenint = (String) SqlManager.getValueFromVectorParam(datiUffint, 7).getValue();
      
      if (altrisog != null && ((cfanac != null && (Long.valueOf(2).equals(altrisog) || Long.valueOf(3).equals(altrisog))) || Long.valueOf(4).equals(altrisog))) {
        stazioneAppaltante.setCodiceFiscale(cfanac);
        stazioneAppaltante.setCodiceAusa(codausaanac);
      } else {
        stazioneAppaltante.setCodiceFiscale(cfein);
        stazioneAppaltante.setCodiceAusa(codausa);
      }
      
      stazioneAppaltante.setCodiceCentroCosto(codcentro);
      if(funzsvolte != null) {
        List<EnumNPA> listFunzioniSvolte = new ArrayList<EnumNPA>();
        listFunzioniSvolte.add(new EnumNPA("funzioniSvolte", String.valueOf(funzsvolte)));
        stazioneAppaltante.setFunzioniSvolte(listFunzioniSvolte);
      }
      stazioneAppaltante.setSaTitolare(Boolean.TRUE);
      
      if (altrisog != null && (Long.valueOf(2).equals(altrisog) || Long.valueOf(3).equals(altrisog))) {
        String getDatiGARALTSOG = "select u.cfein, u.codausa, s.codcentro "
            + "from garaltsog s  "
            + "inner join UFFINT u on s.cenint = u.codein  "
            + "inner join gare g on g.ngara = s.ngara  "
            + "and g.codgar1 = ? "
            + "where exists ( "
            + "    select 1 "
            + "    from garaltsog s1 "
            + "    inner join UFFINT u1 on s1.cenint = u1.codein  "
            + "    inner join gare g1 on g1.ngara = s1.ngara  "
            + "    and g1.codgar1 = ? "
            + "    where u1.cfein = u.cfein "
            + "    and u1.cfein is not null "
            + ") and s.cenint <> ? "
            + "group by u.cfein, u.codausa, s.codcentro ";
        
        String getDatiGARALTSOGIncompl = "select u.cfein, u.codausa, s.codcentro "
            + "from garaltsog s "
            + "inner join UFFINT u on s.cenint = u.codein "
            + "inner join gare g on g.ngara = s.ngara "
            + "and g.codgar1 = ? "
            + "where (u.cfein is null "
            + "    or s.codcentro is null) "
            + "and s.cenint <> ? "
            + "group by u.cfein, u.codausa, s.codcentro ";
        
        List<String> datiGARALTSOG = this.sqlManager.getListVector(getDatiGARALTSOG, new Object[]{codGar,codGar,cenint});
        List<String> datiGARALTSOGIncompl = this.sqlManager.getListVector(getDatiGARALTSOGIncompl, new Object[]{codGar,cenint});
        if((datiGARALTSOG!=null && !datiGARALTSOG.isEmpty())
            || (datiGARALTSOGIncompl!=null && !datiGARALTSOGIncompl.isEmpty())){
          stazioneAppaltante.setSaTitolare(Boolean.FALSE);
          listaStazioniAppaltanti.add(stazioneAppaltante);
          if(datiGARALTSOG!=null && !datiGARALTSOG.isEmpty()) {
            for (int i = 0; i < datiGARALTSOG.size(); i++) {
              stazioneAppaltante = new StazioneAppaltante();
              cfein = (String) SqlManager.getValueFromVectorParam(datiGARALTSOG.get(i), 0).getValue();
              codausa = (String) SqlManager.getValueFromVectorParam(datiGARALTSOG.get(i), 1).getValue();
              codcentro = (String) SqlManager.getValueFromVectorParam(datiGARALTSOG.get(i), 2).getValue();
  
              stazioneAppaltante.setCodiceFiscale(cfein);
              stazioneAppaltante.setCodiceAusa(codausa);
              stazioneAppaltante.setCodiceCentroCosto(codcentro);
              stazioneAppaltante.setSaTitolare(Boolean.TRUE);
              listaStazioniAppaltanti.add(stazioneAppaltante);
            }
          }
          if(datiGARALTSOGIncompl!=null && !datiGARALTSOGIncompl.isEmpty()) {
            for (int i = 0; i < datiGARALTSOGIncompl.size(); i++) {
              stazioneAppaltante = new StazioneAppaltante();
              cfein = (String) SqlManager.getValueFromVectorParam(datiGARALTSOGIncompl.get(i), 0).getValue();
              codausa = (String) SqlManager.getValueFromVectorParam(datiGARALTSOGIncompl.get(i), 1).getValue();
              codcentro = (String) SqlManager.getValueFromVectorParam(datiGARALTSOGIncompl.get(i), 2).getValue();
  
              stazioneAppaltante.setCodiceFiscale(cfein);
              stazioneAppaltante.setCodiceAusa(codausa);
              stazioneAppaltante.setCodiceCentroCosto(codcentro);
              stazioneAppaltante.setSaTitolare(Boolean.TRUE);
              listaStazioniAppaltanti.add(stazioneAppaltante);
            }
          }
        }else {
          listaStazioniAppaltanti.add(stazioneAppaltante);
        }
      } else {
        listaStazioniAppaltanti.add(stazioneAppaltante);
      }
    }
    return listaStazioniAppaltanti;
  }

  public List<EnumNPA> getListaMotivoDerogaByNgara(String ngara) throws SQLException {
    List<EnumNPA> listaMotivoDeroga = null;
    String getG1QUOTADER = "select motderoga from G1QUOTADER where ngara = ? ";
    List<String> datiG1QUOTADER = this.sqlManager.getListVector(getG1QUOTADER, new Object[]{ngara});
    if(datiG1QUOTADER!=null && !datiG1QUOTADER.isEmpty()){
      listaMotivoDeroga = new ArrayList<EnumNPA>();
      for (int i = 0; i < datiG1QUOTADER.size(); i++) {
        Long motderoga = (Long) SqlManager.getValueFromVectorParam(datiG1QUOTADER.get(i), 0).getValue();
        if(motderoga != null) {
          listaMotivoDeroga.add(new EnumNPA("motivoDeroga", String.valueOf(motderoga)));          
        }
      }
    }
    return listaMotivoDeroga;
  }

  public List<EnumNPA> getListaMisurePremialiByNgara(String ngara) throws SQLException {
    List<EnumNPA> listaMisurePremiali = null;
    String getG1MISPREMIALI = "select misura from G1MISPREMIALI where ngara = ? ";
    List<String> datiG1MISPREMIALI = this.sqlManager.getListVector(getG1MISPREMIALI, new Object[]{ngara});
    if(datiG1MISPREMIALI!=null && !datiG1MISPREMIALI.isEmpty()){
      listaMisurePremiali = new ArrayList<EnumNPA>();
      for (int i = 0; i < datiG1MISPREMIALI.size(); i++) {
        Long misura = (Long) SqlManager.getValueFromVectorParam(datiG1MISPREMIALI.get(i), 0).getValue();
        if(misura != null) {
          listaMisurePremiali.add(new EnumNPA("misurePremiali", String.valueOf(misura)));          
        }
      }
    }
    return listaMisurePremiali;
  }

  public boolean presentiUlterioriMisurePremiali(String ngara) throws SQLException {
    List<EnumNPA> listaMisurePremiali = getListaMisurePremialiByNgara(ngara);
    if (listaMisurePremiali == null || listaMisurePremiali.isEmpty())
      return false;
    for (EnumNPA misura : listaMisurePremiali) {
      if (misura.getCodice() != null && !"13".equals(misura.getCodice())) return true;
    }
    return false;
  }

  public Boolean getIsUlterioriMisurePremiali(String ngara) throws Exception {
    if(isApplicazioneVersioneAttiva("3")) {
      return presentiUlterioriMisurePremiali(ngara);
    } else {
      return null;
    }
  }

  public List<Finanziamento> listaFinanziamentiByNgara(String ngara) throws Exception {
    List<Finanziamento> listafinanziamenti = null;
    String getDatiG1dfina = "SELECT tifina,impofi FROM g1dfina where ngara = ? and (tifina is not null or impofi is not null)";
    List<String> datiG1dfina = this.sqlManager.getListVector(getDatiG1dfina, new Object[]{ngara});
    if(datiG1dfina!=null && !datiG1dfina.isEmpty()){
      listafinanziamenti = new ArrayList<Finanziamento>();
      for (int i = 0; i < datiG1dfina.size(); i++) {
        String tifina = (String) SqlManager.getValueFromVectorParam(datiG1dfina.get(i), 0).getValue();
        Double impofi = (Double) SqlManager.getValueFromVectorParam(datiG1dfina.get(i), 1).getValue();
        EnumNPA tipoFinanziamentoEnum = null;
        if(tifina != null) {
          tipoFinanziamentoEnum = new EnumNPA("tipoFinanziamento", tifina);
        }
        listafinanziamenti.add(new Finanziamento(tipoFinanziamentoEnum, impofi));
      }
    }
    return listafinanziamenti;
  }

  public DatiBaseCPV getDatiBaseCpvByNgara(String ngara) throws Exception {
    EnumNPA cpvPrimario = null;
    List<EnumNPA> listaCPVSecondati = null;
    String getDatiGARCPV = "select codcpv, tipcpv from GARCPV where ngara = ? ";
    List<String> datiGARCPV = this.sqlManager.getListVector(getDatiGARCPV, new Object[]{ngara});
    if(datiGARCPV!=null && !datiGARCPV.isEmpty()){
      listaCPVSecondati = new ArrayList<EnumNPA>();
      for (int i = 0; i < datiGARCPV.size(); i++) {
        String codcpv = (String) SqlManager.getValueFromVectorParam(datiGARCPV.get(i), 0).getValue();
        String tipcpv = (String) SqlManager.getValueFromVectorParam(datiGARCPV.get(i), 1).getValue();
        if(tipcpv != null && codcpv != null) {
          if(codcpv.contains("-")) {
            codcpv = codcpv.split("-")[0];
          }
          if("1".equals(tipcpv)) {
            cpvPrimario = new EnumNPA("CPV", codcpv);
          } else {
            listaCPVSecondati.add(new EnumNPA("CPV", codcpv));            
          }
        }
      }
    }
    return new DatiBaseCPV(new EnumNPA("tipoClassificazione", "cpv"), cpvPrimario, listaCPVSecondati);
  }

  public Boolean getBooleanByStringNotNull(String booleano) {
    Boolean value = getBooleanByString(booleano);
    if(value == null) {
      value = Boolean.FALSE;
    }
    return value;
  }

  public static Boolean getBooleanByString(String booleano) {
    Boolean value = null;
    if(booleano != null && !booleano.isEmpty()) {
      if("1".equals(booleano)) {
        value = Boolean.TRUE;
      } else if("2".equals(booleano)) {
        value = Boolean.FALSE;
      }
    }
    return value;
  }
  
  public EnumNPA getOggettoContrattoByTipgen(Long tipgen) {
    EnumNPA oggettoContratto = null;
    switch (String.valueOf(tipgen)) {
    case "1":
      oggettoContratto = new EnumNPA("oggettoContratto", "works");
      break;
    case "2":
      oggettoContratto = new EnumNPA("oggettoContratto", "supplies");
      break;
    case "3":
      oggettoContratto = new EnumNPA("oggettoContratto", "services");
      break;
    default:
      break;
    }
    return oggettoContratto;
  }
  
  public EnumNPA getTipologiaAvvaliamentoByTipoAv(Long tipoav) {
    EnumNPA tipologiaAvvaliamento = null;
    switch (String.valueOf(tipoav)) {
    case "4":
      tipologiaAvvaliamento = new EnumNPA("tipologiaAvvalimento", "1");
      break;
    case "5":
      tipologiaAvvaliamento = new EnumNPA("tipologiaAvvalimento", "2");
      break;
    default:
      break;
    }
    return tipologiaAvvaliamento;
  }
  
  public EnumNPA getOggettoContrattoByTipApp(Long tipapp) {
    EnumNPA oggettoContratto = null;
    
    if(tipapp >= Long.valueOf(100)) {
      oggettoContratto = new EnumNPA("oggettoContratto", "works");
    } else if(tipapp < Long.valueOf(100) && tipapp >= Long.valueOf(10)) {
      oggettoContratto = new EnumNPA("oggettoContratto", "supplies");
    } else if(tipapp < Long.valueOf(10)) {
      oggettoContratto = new EnumNPA("oggettoContratto", "services");
      if(Long.valueOf(4).equals(tipapp)) {
        oggettoContratto = new EnumNPA("oggettoContratto", "serv");
      }
    }
    return oggettoContratto;
  }
  
  public EnumNPA getOggettoContrattoByTipoEle(String tipoele) {
    EnumNPA oggettoContratto = null;
    Long tipo = null;
    tipoele= StringUtils.stripToEmpty(tipoele);
    if(!"".equals(tipoele)) {
    	tipo = Long.valueOf(tipoele);
        if(tipo >= Long.valueOf(100)) {
            oggettoContratto = new EnumNPA("oggettoContratto", "works");
        } else if(tipo < Long.valueOf(100) && tipo >= Long.valueOf(10)) {
            oggettoContratto = new EnumNPA("oggettoContratto", "supplies");
        } else if(tipo < Long.valueOf(10)) {
            oggettoContratto = new EnumNPA("oggettoContratto", "services");
        }
    }
    return oggettoContratto;
  }
  
  public EnumNPA getModalitaAcquisizioneByOggcont(Long oggcont) {
    EnumNPA modalitaAcquizione = null;
    switch (String.valueOf(oggcont)) {
    case "10":
      modalitaAcquizione = new EnumNPA("modalitaAcquisizione", "1");
      break;
    case "11":
      modalitaAcquizione = new EnumNPA("modalitaAcquisizione", "2");
      break;
    case "12":
      modalitaAcquizione = new EnumNPA("modalitaAcquisizione", "3");
      break;
    case "13":
      modalitaAcquizione = new EnumNPA("modalitaAcquisizione", "4");
      break;
    case "14":
      modalitaAcquizione = new EnumNPA("modalitaAcquisizione", "5");
      break;
    default:
      break;
    }
    return modalitaAcquizione;
  }
  
  private String getTipoProceduraOth(String codgar) throws Exception {
    String tipoProcedura = null;
    String getTipgarg = "select tipgarg "
        + "from gare g "
        + "inner join v_gare_genere v on v.codgar = g.codgar1 and v.genere in(1,2,3) "
        + "where g.codgar1 = ? ";
    Long tipgarg = (Long) this.sqlManager.getObject(getTipgarg, new Object[]{codgar});
    if(tipgarg != null) {
      String procedura= inviaVigilanzaManager.getFromTab2("A1z11", tipgarg, false);
      if(procedura != null) {
        if("50".equals(procedura)) {
          tipoProcedura = "oth-single";
        }
        if("51".equals(procedura)) {
          tipoProcedura = "oth-mult";
        }
      }
    }
    return tipoProcedura;
  }

  public EnumNPA getTipoProceduraByIterga(Long iterga, String codgar) throws Exception {
    
    String tipoProcedura = getTipoProceduraOth(codgar);
    if(tipoProcedura == null) {
      switch (String.valueOf(iterga)) {
      case "1":
      case "9":
        tipoProcedura = "open";
        break;
      case "2":
        tipoProcedura = "restricted";
        break;
      case "4":
        tipoProcedura = "neg-w-call";
        break;
      case "3":
      case "5":
      case "6":
        tipoProcedura = "neg-wo-call";
        break;
      default:
        tipoProcedura = "comp-tend";
        break;
      }
    }
    return new EnumNPA("tipoProcedura", tipoProcedura);
  }
  
  private String getTipoProceduraByNgara(String ngara) throws Exception {
    String tipoProcedura = null;
    String getTipgarg = "select tipgarg "
        + "from gare g "
        + "inner join v_gare_genere v on v.codgar = g.codgar1 and v.genere in(100,2,300) "
        + "where g.ngara = ? ";
    Long tipgarg = (Long) this.sqlManager.getObject(getTipgarg, new Object[]{ngara});
    if(tipgarg != null) {
      String procedura= inviaVigilanzaManager.getFromTab2("A1z11", tipgarg, false);
      if(procedura != null) {
        switch (procedura) {
        case "15":
        case "16":
        case "17":
          tipoProcedura = "10";
          break;
        case "50":
          tipoProcedura = "8";
          break;
        case "51":
          tipoProcedura = "7";
          break;
        default:
          break;
        }
      }
    }
    return tipoProcedura;
  }

  public EnumNPA getTipoProceduraByItergaNgara(Long iterga, String ngara) throws Exception {
    
    String tipoProcedura = getTipoProceduraByNgara(ngara);
    if(tipoProcedura == null) {
      switch (String.valueOf(iterga)) {
      case "1":
      case "9":
        tipoProcedura = "6";
        break;
      case "2":
        tipoProcedura = "9";
        break;
      case "4":
        tipoProcedura = "4";
        break;
      case "3":
      case "5":
      case "6":
        tipoProcedura = "5";
        break;
      default:
        tipoProcedura = "8";
        break;
      }
    }
    return new EnumNPA("tipoProcedura", tipoProcedura);
  }
  

  public String getTab2d1ByTab2tip(String tab2tip, String tab2cod) throws Exception {
    String query = "select tab2d1 from tab2 where tab2tip = ? and tab2cod = ? ";
    return (String) this.sqlManager.getObject(query, new Object[]{tab2tip, tab2cod});
  }

  public static String getCriterioAggiudicazione(Long critlicg, Boolean costofisso, String gartel, Long genere) {
    String criteriAggiudicazione = null;
    if(critlicg != null){
      if(Long.valueOf(2).equals(critlicg)){
        if(Boolean.TRUE.equals(costofisso)){
          criteriAggiudicazione = "quality";
        } else {
          criteriAggiudicazione = "cost";
        }
      } else if(Long.valueOf(1).equals(critlicg) || Long.valueOf(3).equals(critlicg)){
        criteriAggiudicazione = "price";
      }
    } else if ((StringUtils.isEmpty(gartel) || !"1".equals(gartel)) && !Arrays.asList(10L, 11L, 12L).contains(genere)) {
      criteriAggiudicazione = "price";
    }
    return criteriAggiudicazione;
  }

  public EnumNPA getCategoria(String ngara, Long tipgen, String codgar, Long genere) throws Exception {

    String codiceGara = ngara;
    if(genere != null && Long.valueOf(300).equals(genere)) {
      Long countCategorieDelLotto = (Long) sqlManager.getObject(QUERY_COUNT_CATEGORIE_NGARA, new Object[] {ngara});
      if (countCategorieDelLotto == null || countCategorieDelLotto == 0)
        codiceGara = codgar;
    }
    EnumNPA categoria = null;
    Vector<?> datiCategoriaPrevalente = this.sqlManager.getVector(QUERY_SELECT_CATEGORIA_PREVALENTE, new Object[]{codiceGara});
    if(datiCategoriaPrevalente!=null && !datiCategoriaPrevalente.isEmpty()){
      String codiceCategoriaPrevalenteSql = (String) SqlManager.getValueFromVectorParam(datiCategoriaPrevalente, 0).getValue();
      Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiCategoriaPrevalente, 1).getValue();
      
      categoria = getEnumCategoria(tipgen,tiplavg,codiceCategoriaPrevalenteSql);
      
    } else if (((Long)2L).equals(tipgen)) {
      categoria = new EnumNPA("categoria", "FB");
    } else if (((Long)3L).equals(tipgen)) {
      categoria = new EnumNPA("categoria", "FS");
    } else {
      categoria = new EnumNPA("categoria", "999");
    }
    return categoria;
  }


  public EnumNPA getEnumCategoria(Long tipgen, Long tiplavg, String codiceCategoriaPrevalenteSql) {
    String codiceCategoriaPrevalente = null;
    EnumNPA categoria = null;
    if(tiplavg == null)
      tiplavg= Long.valueOf(0);
    
    if(Long.valueOf(2).equals(tipgen))
      codiceCategoriaPrevalente="FB";
    else if(Long.valueOf(3).equals(tipgen))
      codiceCategoriaPrevalente="FS";
    else if(Long.valueOf(1).equals(tipgen)) {
      //Gara per lavori
      if(Long.valueOf(1).equals(tiplavg)) {
        codiceCategoriaPrevalente="AA";
        if (codiceCategoriaPrevalenteSql != null 
            && (codiceCategoriaPrevalenteSql.length()<=5  
            && (codiceCategoriaPrevalenteSql.startsWith("OG") || codiceCategoriaPrevalenteSql.startsWith("OS"))
            && isAlfanumericaSenzaSpaziOCaratSpeciali(codiceCategoriaPrevalenteSql))) {
          codiceCategoriaPrevalente = codiceCategoriaPrevalenteSql;            
        }
      } 
      else if(Long.valueOf(2).equals(tiplavg))
        codiceCategoriaPrevalente="FB";
      else if(Long.valueOf(3).equals(tiplavg) || Long.valueOf(5).equals(tiplavg))
        codiceCategoriaPrevalente="FS";
      else if(Long.valueOf(4).equals(tiplavg))
        codiceCategoriaPrevalente="AA";
    }
    
    if (codiceCategoriaPrevalente != null) {
      if(codiceCategoriaPrevalente.startsWith("OG") || codiceCategoriaPrevalente.startsWith("OS")) {
        codiceCategoriaPrevalente = aggiungiSpazio(codiceCategoriaPrevalente);
      }
      categoria = new EnumNPA("categoria", codiceCategoriaPrevalente);
    }
    return categoria;
  }
  
  public String aggiungiSpazio(String input) {
    //controllo se il formato è di questo tipo "AB12C" se è così lo trasformo in "AB 12-C"
    //Else caso in cui non c'è un carattere alla fine "AB12" diventa cosi "AB 12"
    if(input.matches("^[A-Za-z]+\\d+[A-Za-z]+$")) {
      input = input.replaceAll("([a-zA-Z]+)(\\d+)([a-zA-Z]+)", "$1 $2-$3");
    } else {
      input = input.replaceAll("([a-zA-Z]+)(\\d+)", "$1 $2");
    }
    return input;
  }
  
  public Boolean isAlfanumericaSenzaSpaziOCaratSpeciali(String input) {

    Boolean isAlfanumerico = Boolean.FALSE;
    // Definisco l'espressione regolare per controllare che la stringa sia alfanumerica 
    // ma non contenga spazi o caratteri speciali
    String regex = "^[a-zA-Z0-9]*$";
    // Creo un pattern dall'espressione regolare
    Pattern pattern = Pattern.compile(regex);
    
    // Controlla se la stringa è alfanumerica e non contiene spazi o caratteri speciali
    if (pattern.matcher(input).matches()) {
      isAlfanumerico = Boolean.TRUE;
    }
    return isAlfanumerico;
  }
  
  public List<EnumNPA> getCategorieScorporabili(String ngara, Long tipgen, String codgar, Long genere) throws Exception {

    String codiceGara = ngara;
    if(genere != null && Long.valueOf(300).equals(genere)) {
      Long countCategorieDelLotto = (Long) sqlManager.getObject(QUERY_COUNT_CATEGORIE_NGARA, new Object[] {ngara});
      if (countCategorieDelLotto == null || countCategorieDelLotto == 0)
        codiceGara = codgar;
    }
    List<EnumNPA> categorie = null;

    String select = "select op.catoff,cs.tiplavg "
            + "from opes op, cais cs "
            + "where op.catoff = cs.caisim "
            + "and op.ngara3 = ? ";
    List<String> datiCategorieScorporabili = this.sqlManager.getListVector(select, new Object[]{codiceGara});

    if(datiCategorieScorporabili!=null && !datiCategorieScorporabili.isEmpty()){
      categorie = new ArrayList<EnumNPA>();
      for (int i = 0; i < datiCategorieScorporabili.size(); i++) {
        String codiceCategoriaPrevalenteSql = (String) SqlManager.getValueFromVectorParam(datiCategorieScorporabili.get(i), 0).getValue();
        Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiCategorieScorporabili.get(i), 1).getValue();
        categorie.add(getEnumCategoria(tipgen,tiplavg,codiceCategoriaPrevalenteSql));
      }
    }
    return categorie;
  }
  
  public Boolean isOpzioniRinnoviMAGGIOREdiZero(Double imprin, Double imppror, Double impaltro) {
    Boolean returnValue = Boolean.FALSE;
    Double somma = Double.valueOf(0);
    if(imprin != null) {
      somma += imprin;
    }
    if(imppror != null) {
      somma += imppror;
    }
    if(impaltro != null) {
      somma += impaltro;
    }
    if(somma > 0) {
      returnValue = Boolean.TRUE;
    }
    return returnValue;
  }

  public String getStringDateFormat(Date data) {
    String formattedDate = null;
    if(data != null) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      formattedDate = dateFormat.format(data);      
    }
    return formattedDate;
  }

  public String unisciDataeOra(String dataScadenzaPresentazioneOfferta, String oraScadenzaPresentazioneOfferta) {
    String dataFormattata = null;
    if(dataScadenzaPresentazioneOfferta != null) {
      Instant instant = Instant.parse(dataScadenzaPresentazioneOfferta);
      Long ora = Long.valueOf(0);
      Long minuti = Long.valueOf(0);
      if(oraScadenzaPresentazioneOfferta != null && oraScadenzaPresentazioneOfferta.contains(":") && oraScadenzaPresentazioneOfferta.split(":").length == 2) {
        ora = Long.valueOf(oraScadenzaPresentazioneOfferta.split(":")[0]);
        minuti = Long.valueOf(oraScadenzaPresentazioneOfferta.split(":")[1]);
      }
      LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
      localDateTime = localDateTime.plusHours(ora).plusMinutes(minuti);
      
      DateTimeFormatter formatterD = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
      dataFormattata = localDateTime.format(formatterD);
    }
    return dataFormattata;
  }
  
  public Long getGenereByCodgar(String codgar) throws Exception {
    String genereSql = "select genere from v_gare_genere where codgar = ? ";
    return (Long) this.sqlManager.getObject(genereSql, new Object[]{codgar});
  }

  public String getSubAppalto(String codgar) throws Exception {
    String subAppalto = "no";

    Long genere = getGenereByCodgar(codgar);
    
    String queryGare = "select subgar from gare where codgar1 = ? and ngara = codgar1 ";
    if(genere != null && Long.valueOf(2).equals(genere)) {
      queryGare = "select subgar from gare where codgar1 = ? ";
    } 
    
    Boolean subgar = getBooleanByString((String) this.sqlManager.getObject(queryGare, new Object[]{codgar}));
    
    if(subgar != null && Boolean.TRUE.equals(subgar)) {
      subAppalto = "yes";
    }
    
    return subAppalto;
  }
  
  public String getCodIstat(String codgar, String ngara) throws Exception {
    String genereSql = "select genere from v_gare_genere where codgar = ? and genere in (1,2,3) ";
    Long genere = (Long) this.sqlManager.getObject(genereSql, new Object[]{codgar});
    
    String queryGare = "select locint from gare where codgar1 = ? and ngara = ?";
    if(genere != null && Long.valueOf(3).equals(genere)) {
      queryGare = "select locint from gare where codgar1 = ? and ngara = codgar1 and ngara = ? ";
      ngara = codgar;
    }
    return (String) this.sqlManager.getObject(queryGare, new Object[]{codgar, ngara});
  }

  public String getUrlSQL(String codGar, String ngara, String scheda) throws Exception {
    DatiBaseDocumentiOptional doc = getDatiBaseDocumentiSQL(codGar, ngara, scheda);
    String url = null;
    if (doc != null && !"".equals(doc.getUrl())){
      url = doc.getUrl();
    }
    return url;
  }
  
  public String togliDollaro(String codGar) {
    String codiceSenzaDollaro = codGar;
    if(codGar.contains("$"))
      codiceSenzaDollaro = codGar.replace("$", "");
    return codiceSenzaDollaro;
  }
  
  public DatiBaseDocumentiOptional getDatiBaseDocumentiSQL(String codGar, String ngara, String scheda) throws Exception {
    Long genere = null;
    if(ngara != null) {
      String genereSql = "select genere from v_gare_genere where codgar = ? and codice = ? ";
      genere = (Long) this.sqlManager.getObject(genereSql, new Object[]{codGar, ngara});
    } else {
      String genereSql = "select genere, codice "
          + "from v_gare_genere "
          + "where codgar = ? "
          + "and genere in (1,2,3) ";
      Vector<?> datiVGenere = this.sqlManager.getVector(genereSql, new Object[]{codGar});
      
      if(datiVGenere!=null && !datiVGenere.isEmpty()){
        genere = (Long) SqlManager.getValueFromVectorParam(datiVGenere, 0).getValue();
        ngara = (String) SqlManager.getValueFromVectorParam(datiVGenere, 1).getValue();
      }
    }
    String url = ConfigManager.getValore("portaleAppalti.urlPubblica");
    String path = ConfigManager.getValore("invioScp.urlProcedurePortaleAppalti");

    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    if (!path.endsWith("/")) {
      path = path +"/";
    }
    
    switch (String.valueOf(genere)) {
    case "2": //gara a lotto unico
    case "10":
    case "11":
    case "20":
      url += path + ngara;
      break;
    case "1":
    case "100":
    case "3":
    case "300":
      url += path + codGar;
      break;
    default:
      break;
    }

    DatiBaseDocumentiOptional baseDocumentiOptional = null;
    if(!url.equals(ConfigManager.getValore("portaleAppalti.urlPubblica"))) {
      baseDocumentiOptional = new DatiBaseDocumentiOptional(url, null);
    }
    return baseDocumentiOptional;
  }
  
  public Double getOffertaMassimoRibasso(String codgar, String ngara) throws Exception {
    Double resOffMaxRib = null;
    Object objOffMaxRib = this.sqlManager.getObject("select min(RIBAUO) "
            + "from DITG "
            + "where ammgar = '1' "
            + "and codgar5 = ? "
            + "and ngara5 = ? ", new Object[]{codgar, ngara});

	Double offMaxRib = checkDoubleValue(objOffMaxRib);

	if(offMaxRib!=null && offMaxRib.doubleValue()<=0) {
		resOffMaxRib = Math.abs(offMaxRib);
	}
    return resOffMaxRib;
  }

  public Double getOffertaMinimoRibasso(String codgar, String ngara) throws Exception {
    Double resOffMaxRib = null;
    Object objOffMaxRib = this.sqlManager.getObject("select max(RIBAUO) "
            + "from DITG "
            + "where ammgar = '1' "
            + "and codgar5 = ? "
            + "and ngara5 = ? ", new Object[]{codgar, ngara});

    Double offMaxRib = checkDoubleValue(objOffMaxRib);

    if(offMaxRib!=null && offMaxRib.doubleValue()<=0) {
      resOffMaxRib = Math.abs(offMaxRib);
    }
    return resOffMaxRib;
  }

  public Double getValMax(String codgar) throws Exception{
    String toAppend=" and v.ngara is null";
    if(codgar.charAt(0) == '$')
      toAppend = "";
    Double valmax = Double.valueOf(0);
    try {
      Object datiGara = this.sqlManager.getObject("select v.valmax from v_gare_importi v where v.codgar=?"+toAppend, new Object[] {codgar});
      if(datiGara!=null){
        valmax = checkDoubleValue(datiGara);
      }
    } catch (SQLException e) {
      throw new Exception("Errore nella determinazione del valmax per la gara " +codgar);
    }
    return valmax;
  }
  
  public String getNotGarByCodgarSql(String codgar) throws Exception {
    String getNotgar = "select not_gar from gare where codgar1 = ? ";
    return (String) this.sqlManager.getObject(getNotgar, new Object[]{codgar});
  }
  
  public Double checkDoubleValue(Object value) {
    Double valueDouble = null;
    if (value instanceof Number) {
        valueDouble = ((Number) value).doubleValue();
    }
    return valueDouble;
  }

  
  public Long getDispopartByCodgarSql(String codgar) throws Exception {
    Long dispopart = null;
    String getNotgar = "select dispopart from gare1 where codgar1 = ? ";
    List<String> datiForDispopart = this.sqlManager.getListVector(getNotgar, new Object[]{codgar});
    if(datiForDispopart!=null && !datiForDispopart.isEmpty()){
      for (int i = 0; i < 1; i++) { //prendo il primo sempre
        dispopart = (Long) SqlManager.getValueFromVectorParam(datiForDispopart.get(i), 0).getValue();
      }
    }
    return dispopart;
  }
  
  public List<Offerte> getListaOfferte(String codgar, String ngara, Boolean procedutaAnnullata) throws Exception {
    //Offerte
    String getDatiOfferte = "select dg.impoff, dg.dittao, ga.ditta, dg.codcnel, ga.modlicg, "
        + "dg.riboepv, dg.ribauo, ga1.escauto, dg.staggi, dg.idpartecipnpa, ga.limmax, "
        + "ga1.costofisso, ga.sicinc, ga.onsogrib, ga.impapp, ga.impsic, ga.onprge, ga.impnrl, dg.ngara5, ga.iaggiu "
        + ", ga.riboepv, ga.ribagg, ditgaq.ribagg "
        + "from ditg dg "
        + "inner join impr i on i.codimp = dg.dittao "
        + "inner join gare ga on ga.codgar1 = dg.codgar5 and ga.ngara = dg.ngara5 "
        + "inner join gare1 ga1 on ga1.codgar1 = dg.codgar5 and ga1.ngara = dg.ngara5 "
        + "left join ditgaq on ditgaq.ngara = dg.ngara5 and ditgaq.dittao = dg.dittao "
        + "where dg.codgar5 = ? "
        + "and dg.ngara5 = ? ";
      if(Boolean.TRUE.equals(procedutaAnnullata)) {
        getDatiOfferte += "and (dg.fasgar is null or (dg.fasgar is not null and dg.fasgar > 1)) ";
      } else {
        getDatiOfferte += "and (dg.fasgar is null or (dg.fasgar is not null and dg.fasgar > 6)) ";
      }
      getDatiOfferte += "order by dg.numordpl";

    List<String> datiOfferte = this.sqlManager.getListVector(getDatiOfferte, new Object[]{codgar, ngara});
    List<Offerte> listaOfferte = new ArrayList<Offerte>();
    for (int y = 0; y < datiOfferte.size(); y++) {
      Offerte offerta = new Offerte();
      Double impoff = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 0).getValue();
      String dittao = (String) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 1).getValue();
      String ditta = (String) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 2).getValue();
      String dittacnel = (String) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 3).getValue();
      Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 4).getValue();
      Double riboepv = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 5).getValue();
      Double ribauo = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 6).getValue();
      Long escauto = (Long) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 7).getValue();
      Long staggi = (Long) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 8).getValue();
      String idpartecipnpa = (String) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 9).getValue();
      Double limmaxx = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 10).getValue();
      Boolean costofisso = getBooleanByString((String) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 11).getValue());
      Boolean sicinc = getBooleanByString((String) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 12).getValue());
      Double impapp = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 14).getValue();
      Double impsic = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 15).getValue();
      ngara = (String) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 18).getValue();
      Double iaggiu = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 19).getValue();
      Double riboepvGare = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 20).getValue();
      Double ribaggGare = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 21).getValue();
      Double ribaggDitgaq = (Double) SqlManager.getValueFromVectorParam(datiOfferte.get(y), 22).getValue();

      offerta.setIdPartecipante(idpartecipnpa);

      if(modlicg != null && Long.valueOf(6).equals(modlicg) && costofisso != null && Boolean.TRUE.equals(costofisso)) {
        offerta.setImporto(impapp);
      } else if(impoff != null && impoff > Double.valueOf(0)) {
        if(sicinc != null && Boolean.FALSE.equals(sicinc)) {
          Double somma = Double.valueOf(0);
          if(impsic != null) {
            somma += impsic;
          }
          somma += impoff;
          offerta.setImporto((Double)UtilityNumeri.arrotondaNumero(somma, Integer.valueOf(5)));
        } else {
          offerta.setImporto((Double)UtilityNumeri.arrotondaNumero(impoff, Integer.valueOf(5)));
        }
      } else {
        
        double ribassoVal = 0;
        if(modlicg != null && Long.valueOf(6).equals(modlicg)) {
          if(riboepv!=null) {
            ribassoVal = riboepv.doubleValue();
          }
        } else {
          if(ribauo!=null) {
            ribassoVal = ribauo.doubleValue();
          }
        }
        
        impoff = this.pgManagerEst1.calcoloImpoffDaRibasso(ngara, ribassoVal);
        
        offerta.setImporto((Double)UtilityNumeri.arrotondaNumero(impoff, Integer.valueOf(5)));
      }

      boolean isDittaAggiudicataria = false;
      boolean isDittaAggiudicatariaAQ = false;
      offerta.setAggiudicatario(Boolean.FALSE);
      if(dittao != null && ditta != null) {
        isDittaAggiudicatariaAQ = Boolean.TRUE.equals(isDittaAggiudicatariaSQL(dittao, ngara));
        isDittaAggiudicataria = ditta.equals(dittao) || isDittaAggiudicatariaAQ;
        if(isDittaAggiudicataria) {
          offerta.setAggiudicatario(Boolean.TRUE);
          offerta.setPosizioneGraduatoria(1);
          if(isDittaAggiudicatariaAQ) {
            String sql = "select iaggiu from ditgaq where dittao = ? and ngara = ? ";
            offerta.setImporto((Double) this.sqlManager.getObject(sql, new Object[]{dittao, ngara}));
          } else {
            offerta.setImporto(iaggiu);
          }
        }
      }

      if(dittacnel != null) {
        offerta.setCcnl(dittacnel);
      }

      Double ribassoOffertaEconomica = null;
      if (isDittaAggiudicataria) {
        if (modlicg != null && modlicg == 6) {
          if (isDittaAggiudicatariaAQ)
            ribassoOffertaEconomica = ribaggDitgaq;
          else
            ribassoOffertaEconomica = riboepvGare;
        } else {
          if (isDittaAggiudicatariaAQ)
            ribassoOffertaEconomica = ribaggDitgaq;
          else
            ribassoOffertaEconomica = ribaggGare;
        }
      } else {
        if (Boolean.TRUE.equals(costofisso) && modlicg != null && modlicg == 6) {
          ribassoOffertaEconomica = 0D;
        } else if (modlicg != null && modlicg == 6) {
          ribassoOffertaEconomica = riboepv;
        } else {
          ribassoOffertaEconomica = ribauo;
        }
      }
      if (ribassoOffertaEconomica == null) {
        String cifreRibasso = this.pgManagerEst1.getNumeroDecimaliRibasso(codgar);
        if (impoff != null && StringUtils.isNotEmpty(cifreRibasso))
          ribassoOffertaEconomica = pgManagerEst1.calcoloRibassoDaImpoff(ngara, impoff,cifreRibasso);
        if (ribassoOffertaEconomica == null)
          ribassoOffertaEconomica = 0D;
      }
      ribassoOffertaEconomica = (Double) UtilityNumeri.arrotondaNumero(ribassoOffertaEconomica, 5);
      if(ribassoOffertaEconomica <= 0D) {
        offerta.setOffertaEconomica(Math.abs(ribassoOffertaEconomica));
      } else {
        offerta.setOffertaInAumento(ribassoOffertaEconomica);
      }
      if(limmaxx!= null && !Double.valueOf(0).equals(limmaxx)) {
        if(ribassoOffertaEconomica < limmaxx) {
          offerta.setOffertaMaggioreSogliaAnomalia(Boolean.TRUE);
        } else {
          offerta.setOffertaMaggioreSogliaAnomalia(Boolean.FALSE);
        }
      }

      if(escauto != null && (Long.valueOf(1).equals(escauto)
          || Long.valueOf(2).equals(escauto) || Long.valueOf(5).equals(escauto))
          && staggi != null && Long.valueOf(3).equals(staggi)) {
        offerta.setImpresaEsclusaAutomaticamente(Boolean.TRUE);
      } else {
        offerta.setImpresaEsclusaAutomaticamente(Boolean.FALSE);
      }

      if(staggi != null && Long.valueOf(3).equals(staggi)) {
        offerta.setOffertaAnomala(Boolean.TRUE);
      } else {
        offerta.setOffertaAnomala(Boolean.FALSE);
      }
      listaOfferte.add(offerta);
    }
    return listaOfferte;
  }

  public void insertJsonNpa(String codiceAppalto, String codGar, String ngara, String scheda, String jsonScheda, List<String> listaCodiciLotto) throws Exception {
    Long idScheda = controllaSchedaSeConfigurata(scheda);
    Long idG1NPAFORM = controllaInserisciG1NPAFORM(codGar, idScheda,ngara);
    inserisciAggiornaG1NPASCHEDA(codiceAppalto, ngara, jsonScheda, idG1NPAFORM);
  }

  public Long controllaSchedaSeConfigurata(String scheda) throws Exception {
    String selectG1CFNPA = "SELECT * FROM G1CF_NPA WHERE NOME_SCHEDA = ?";
    Long idScheda = (Long) this.sqlManager.getObject(selectG1CFNPA, new Object[]{scheda});
    if(idScheda == null) {
      throw new NotFoundException("La scheda \""+scheda+"\" non e' configurata in G1CF_NPA");
    }
    return idScheda;
  }

  public Long controllaInserisciG1NPAFORM(String codGar, Long idScheda, String ngara) throws Exception {
    String selectG1NPAFORM = "select n.id "
        + "from g1npaform n "
        + "inner join g1npascheda s on n.id = s.idform "
        + "where n.codgar = ? "
        + "and n.idcfnpa = ? "
        + "and s.statonpa = ? ";
    Object[] parametri = new Object[]{codGar, idScheda, Long.valueOf(1)};
    if (StringUtils.isNotEmpty(ngara)) {
      selectG1NPAFORM += "and s.ngara = ?";
      parametri = new Object[]{codGar, idScheda, Long.valueOf(1),ngara};
    }
    return (Long) this.sqlManager.getObject(selectG1NPAFORM, parametri);
  }

  public void inserisciAggiornaG1NPASCHEDA(String codiceAppalto, String ngara, String jsonScheda, Long idG1NPAFORM) throws Exception {
    String selectG1NPASCHEDA = "";
    Long idG1NPASCHEDA = null;
    if(ngara != null) {
      selectG1NPASCHEDA = "SELECT id FROM G1NPASCHEDA WHERE ngara = ? AND idform = ?";
      idG1NPASCHEDA = (Long) this.sqlManager.getObject(selectG1NPASCHEDA, new Object[]{ngara, idG1NPAFORM});
    } else {
      selectG1NPASCHEDA = "SELECT id FROM G1NPASCHEDA WHERE ngara is null AND idform = ?";
      idG1NPASCHEDA = (Long) this.sqlManager.getObject(selectG1NPASCHEDA, new Object[]{idG1NPAFORM});
    }

    String insertG1NPAFORM = "UPDATE G1NPASCHEDA SET textnpa=?, wsresponse=null WHERE id = ? ";
    this.sqlManager.update(insertG1NPAFORM, new Object[] { jsonScheda, idG1NPASCHEDA });
    
  }
  
  public void tracciaJson(String path, String json) {
    File newTextFile = new File(path);
    try (FileWriter fw = new FileWriter(newTextFile)){
      fw.write(json);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public Long getNumeroOfferteByDittaNgara(String codgar, String ngara, Boolean proceduraAnnullata) throws Exception {
    String query = "select count(*) "
        + "from DITG "
        + "where ";
        if(Boolean.TRUE.equals(proceduraAnnullata)) {
          query += "((fasgar is not null and fasgar > 1) or fasgar is null) ";
        } else {
          query += "((ammgar is not null and ammgar = '1') or ammgar is null) ";
        }
        query += "and codgar5 = ? "
        + "and ngara5 = ? ";
    return (Long) this.sqlManager.getObject(query, new Object[]{codgar, ngara});
  }
  
  public Boolean contieneEform(String codiceScheda) throws Exception {
    Boolean contieneEform = Boolean.FALSE;
    String query = "select eform from g1cf_npa where nome_scheda = ? ";
    String codEform = (String) this.sqlManager.getObject(query, new Object[]{codiceScheda});
    if(codEform != null) {
      contieneEform = Boolean.TRUE;
    }
    return contieneEform;
  }
 
  private java.util.Date getDataVersioneAttiva(String tab2tip) throws Exception {
    String strDataVersionamentoAnac = (String) sqlManager.getObject(
      "select TAB2D2 from TAB2 where TAB2COD='A1z20' and TAB2TIP=?", new Object[] {tab2tip});
  
    java.util.Date dataVersionamentoAnac = null;
    if (StringUtils.isNotEmpty(strDataVersionamentoAnac)) {
      dataVersionamentoAnac = UtilityDate.convertiData(strDataVersionamentoAnac.substring(0, 10), 
        UtilityDate.FORMATO_GG_MM_AAAA);
    }
    return dataVersionamentoAnac;
  }
  
  public Boolean isApplicazioneVersioneAttiva(String tab1tip) throws Exception {
    Boolean isVersionamentoAnac = Boolean.FALSE;
    java.util.Date dataVersionamentoAnac = getDataVersioneAttiva(tab1tip);
    java.util.Date dataOdierna = new java.util.Date();

    if((dataVersionamentoAnac != null) 
        && (dataOdierna.after(dataVersionamentoAnac) || dataOdierna.equals(dataVersionamentoAnac))) {
      isVersionamentoAnac = Boolean.TRUE;
    }
    return isVersionamentoAnac;
  }
  
  public Long getNumeroImpreseEsclusePerInsufficientiGiustificazioni(String codgar, String ngara) throws Exception {
    return (Long) this.sqlManager.getObject("select distinct(dga.dittao) from ditg dg "
        + "inner join ditgammis dga on dg.codgar5 = dga.codgar and dg.ngara5 = dga.ngara "
        + "where dg.codgar5 = ? "
        + "and dg.ngara5 = ? "
        + "and dga.FASGAR > 1 "
        + "and dga.AMMGAR = 9 ", new Object[]{codgar, ngara});
  }
  
  public Map<Boolean, List<String>> getCupLottoAcquisizioneCup(String cupprg, String ngara) throws Exception {
    Boolean acquisizioneCup = Boolean.FALSE;
    List<String> listaCupLotto = new ArrayList<>();

    if(cupprg != null && !cupprg.isEmpty()) {
      acquisizioneCup = Boolean.TRUE;
      listaCupLotto.add(cupprg);
    }

    String getDatiGareCup = "select cup from garecup where ngara = ?";
    List<String> datiGareCup = this.sqlManager.getListVector(getDatiGareCup, new Object[]{ngara});
    if(datiGareCup!=null && !datiGareCup.isEmpty()){
      for (int y = 0; y < datiGareCup.size(); y++) {
        String cup = (String) SqlManager.getValueFromVectorParam(datiGareCup.get(y), 0).getValue();
        if(cup != null && !cup.isEmpty()) {
          listaCupLotto.add(cup);
        }
      }
      acquisizioneCup = Boolean.TRUE;
    }

    Map<Boolean, List<String>> risultato = new HashMap<>();
    risultato.put(acquisizioneCup, listaCupLotto);

    return risultato;
  }
  
  public String getSceltaContraente(Boolean accqua, Boolean isadesione, String ngara, Long iterga) throws Exception {
    String tipoProcedura = null;
    if(accqua != null && Boolean.TRUE.equals(accqua)) {
      tipoProcedura = "11";
    } else if(isadesione != null && Boolean.TRUE.equals(isadesione)) {
      tipoProcedura = "12";
    } else {
      //In questo caso non sara' mai null, quindi posso accedere direttamente al codice
      tipoProcedura = getTipoProceduraByItergaNgara(iterga, ngara).getCodice();
    }
    return tipoProcedura;
  }

  private String getCodiceIsoNazione(Long codiceNaz) throws SQLException {
    String sql = "select CODISO from G_NAZIONI where codice=?";
    return (String) this.sqlManager.getObject(sql, new Object[]{codiceNaz});
  }

  //APPALTI-1821 aggiungo ditte ausiliarie (avvalimento). Per ora usato solo sulla S2 ma potenzialmente funziona per qualsiasi scheda col campo "partecipanti"
  public void aggiungiDitteAvvalimento(List<Partecipante> listaPartecipanti, String idpartecipnpa, String ngara, String codiceDitta) throws SQLException {
    List<Vector>  datiImprAvvalim = sqlManager.getListVector("select i.tipimp, i.nomest, i.cfimp, i.nazimp from impr i, ditgavval d where i.codimp=d.dittaav and d.ngara=? and d.dittao=?", new Object[]{ngara,codiceDitta});
    if(datiImprAvvalim!=null && !datiImprAvvalim.isEmpty()) {
      for (int j = 0; j < datiImprAvvalim.size(); j++) {
        Partecipante partecipante = new Partecipante();
        Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 0).getValue();
        String nomest = (String) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 1).getValue();
        String cfimp = (String) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 2).getValue();
        Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 3).getValue();
        partecipante.setCodiceFiscale(cfimp);
        partecipante.setDenominazione(nomest);
        partecipante.setRuoloOE(new EnumNPA("ruoloOE", "4"));
        partecipante.setTipoOE(calcolaTipoOE(tipimp));
        partecipante.setIdPartecipante(idpartecipnpa);
        if (nazimp != null) {
          partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
        } else {
          partecipante.setPaeseOperatoreEconomico("IT");
        }
        partecipante.setAvvalimento(false);
        listaPartecipanti.add(partecipante);
      }
    }
  }

  //APPALTI-1911 overload del metodo sopra perché si gestisce la classe PartecipanteAD piuttosto che la classe Partecipante
  public void aggiungiDitteAvvalimento(List<PartecipanteAD> listaPartecipanti, String idpartecipnpa, String ngara, String codiceDitta, Double importo) throws SQLException {
    List<Vector>  datiImprAvvalim = sqlManager.getListVector("select i.tipimp, i.nomest, i.cfimp, i.nazimp from impr i, ditgavval d where i.codimp=d.dittaav and d.ngara=? and d.dittao=?", new Object[]{ngara,codiceDitta});
    if(datiImprAvvalim!=null && !datiImprAvvalim.isEmpty()) {
      for (int j = 0; j < datiImprAvvalim.size(); j++) {
        PartecipanteAD partecipante = new PartecipanteAD();
        Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 0).getValue();
        String nomest = (String) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 1).getValue();
        String cfimp = (String) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 2).getValue();
        Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiImprAvvalim.get(j), 3).getValue();
        partecipante.setCodiceFiscale(cfimp);
        partecipante.setDenominazione(nomest);
        partecipante.setRuoloOE(new EnumNPA("ruoloOE", "4"));
        partecipante.setTipoOE(calcolaTipoOE(tipimp));
        partecipante.setIdPartecipante(idpartecipnpa);
        partecipante.setImporto(importo);
        if (nazimp != null) {
          partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
        } else {
          partecipante.setPaeseOperatoreEconomico("IT");
        }
        partecipante.setAvvalimento(false);
        listaPartecipanti.add(partecipante);
      }
    }
  }
  ///APPALTI-1911 aggiungo consorziate esecutrici. Per ora il metodo è usato per le schede AD (no AD5)
  public void aggiungiConsorziateEsecutrici(List<PartecipanteAD> listaPartecipanti, String idpartecipnpa, String ngara, String codiceDitta,Double importo) throws SQLException {
    List<Vector>  datiConsorziate = sqlManager.getListVector("select i.tipimp, i.nomest, i.cfimp, i.nazimp from impr i, ragdet r where i.codimp=r.coddic and r.ngara=? and r.codimp=?", new Object[]{ngara,codiceDitta});
    if(datiConsorziate!=null && !datiConsorziate.isEmpty()) {
      for (int j = 0; j < datiConsorziate.size(); j++) {
        PartecipanteAD partecipante = new PartecipanteAD();
        Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiConsorziate.get(j), 0).getValue();
        String nomest = (String) SqlManager.getValueFromVectorParam(datiConsorziate.get(j), 1).getValue();
        String cfimp = (String) SqlManager.getValueFromVectorParam(datiConsorziate.get(j), 2).getValue();
        Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiConsorziate.get(j), 3).getValue();
        partecipante.setCodiceFiscale(cfimp);
        partecipante.setDenominazione(nomest);
        partecipante.setRuoloOE(new EnumNPA("ruoloOE", "11"));
        partecipante.setTipoOE(calcolaTipoOE(tipimp));
        partecipante.setIdPartecipante(idpartecipnpa);
        partecipante.setImporto(importo);
        if (nazimp != null) {
          partecipante.setPaeseOperatoreEconomico(getCodiceIsoNazione(nazimp));
        } else {
          partecipante.setPaeseOperatoreEconomico("IT");
        }
        partecipante.setAvvalimento(false);
        listaPartecipanti.add(partecipante);
      }
    }
  }

  public static boolean isTipnegDataFineNonNulla(Long tipneg) {
    return tipneg != null && ((tipneg>= 33L && tipneg<= 46L) || tipneg.equals(25L));
  }

  public AnacFormPL2 getAnacFormPL2(Long idScheda, String codgar, String nomeScheda) throws Exception {
    AnacFormPL2 anacFormPL2 = null;
    String selectDatiTornata = null;
    String campiComuni = ", t.dinvit, t.accqua, t.ricastae, v.genere, t.gartel ";

    switch (nomeScheda) {
      case "PL2_1":
      case "PL2_2":
          anacFormPL2 = new AnacFormPL2_1();
          selectDatiTornata = "select ga.tipoapp, t.codnuts, ga.oggetto, ga.ngara, t.issda"
                + campiComuni + "from torn t "
                + "inner join gareavvisi ga on t.codgar = ga.codgar "
                + "inner join v_gare_genere v on v.codgar = t.codgar "
                + "where t.codgar = ? ";
          break;
      case "PL2_7":
      case "PL2_8":
          anacFormPL2 = new AnacFormPL2_7();
        selectDatiTornata = "select t.tipgen, t.iterga, t.destor as oggetto"
                + campiComuni + "from torn t "
                + "inner join v_gare_genere v on v.codgar = t.codgar and v.genere in(1,2,3) "
                + "where t.codgar = ? ";
          break;
      default:
        throw new Exception("Scheda non gestita");
    }

    Map parametriTornata = this.sqlManager.getHashMap(selectDatiTornata, new Object[]{codgar});
    parametriTornata.put("idScheda", idScheda);
    parametriTornata.put("CODGAR",  new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar));
    parametriTornata.put("nomeScheda", nomeScheda);
    parametriTornata.put("npaFunzioniManager", this);

    if (Arrays.asList("PL2_7","PL2_8").contains(nomeScheda)) {
      parametriTornata.put("ISSDA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "3"));
      if (codgar.startsWith("$"))
        parametriTornata.put("OGGETTO",new JdbcParametro(JdbcParametro.TIPO_TESTO, getNotGarByCodgarSql(codgar)));
      parametriTornata.put("importo", getValMax(codgar));
    } else {
      Double importo = (Double) sqlManager.getObject("select impavv from gareavvisi where codgar=?", new Object[] {codgar});
      parametriTornata.put("importo", importo);
    }

    Long genereGara = (Long) ((JdbcParametro) parametriTornata.get("GENERE")).getValue();

    List datiLotti = getMappaDatiLottiSQL(idScheda, codgar, false, genereGara);

    anacFormPL2.setStazioniAppaltanti(getListaStazioniAppaltantiByCodGar(codgar));
    anacFormPL2.setAppalto(createInstance(anacFormPL2.getAppaltoClass(), parametriTornata));
    anacFormPL2.setLotti(createListOfIstances(anacFormPL2.getLottoClass(), parametriTornata, datiLotti));
    return anacFormPL2;
  }

  private Object createInstance(Class classe, Map parameters) throws Exception {
    if (classe == null)
      return null;
    return classe.getConstructor(Map.class).newInstance(parameters);
  }

  private List<?> createListOfIstances(Class classe, Map parameters, List<Map> datiLotti) throws Exception {
    if (classe == null)
      return null;
    List lotti = new ArrayList<Object>();
    for (Map parametriLotto : datiLotti) {
      lotti.add(classe.getConstructor(Map.class,Map.class).newInstance(parameters, parametriLotto));
    }
    return lotti;
  }

  public static Object getValoreParametro(Map parameters, String key) {
    Object parametro = parameters.get(key);
    if (parametro instanceof JdbcParametro) {
      return ((JdbcParametro) parametro).getValue();
    }
    return null;
  }

  public DatiBaseAggiuntivi getDatiBaseAggiuntivi(String affiris) throws Exception {
    return new DatiBaseAggiuntivi(getTab2d1ByTab2tip(affiris, "A1z26"));
  }

  public DatiBaseContratto getDatiBaseContratto(String codnuts) throws Exception {
    EnumNPA codNUTSNPA = null;
    if(codnuts == null || codnuts.isEmpty()) {
      if(!isApplicazioneVersioneAttiva("2")){
        codNUTSNPA = new EnumNPA("codNUTS", "999");
      }
    } else {
      codNUTSNPA = new EnumNPA("codNUTS", codnuts);
    }
    return new DatiBaseContratto(codNUTSNPA);
  }

  public static EnumNPA getEnumSettore(String settore) {
    String codice = "S".equals(settore) ? "2" : "1";
    return new EnumNPA("settore", codice);
  }

  public static EnumNPA getDerogaQualificazione(Long derogasa) {
    if (derogasa != null)
      return new EnumNPA("motivoDerogaQlf",String.valueOf(derogasa));
    else return null;
  }
}
