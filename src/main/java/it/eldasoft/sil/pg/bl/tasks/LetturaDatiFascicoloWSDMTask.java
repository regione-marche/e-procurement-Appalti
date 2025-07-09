/*
 * Created on 26/03/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
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
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class LetturaDatiFascicoloWSDMTask {

  static Logger logger = Logger.getLogger(LetturaDatiFascicoloWSDMTask.class);

  private SqlManager sqlManager;
  private GestioneWSDMManager gestioneWSDMManager;
  private AnalizzaArchiviazioneDocumentiManager analizzaArchiviazioneDocumentiManager;


  /**
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  public void setAnalizzaArchiviazioneDocumentiManager(AnalizzaArchiviazioneDocumentiManager analizzaArchiviazioneDocumentiManager) {
    this.analizzaArchiviazioneDocumentiManager = analizzaArchiviazioneDocumentiManager;
  }

  public void letturaDatiFascicoloLapisopera() throws GestoreException {

    if (WebUtilities.isAppNotReady()) {
      return;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("letturaDatiFascicoloLapisopera: inizio metodo");
    }

    int livEvento=1;
    List<String> errMsgs = new ArrayList<>();
    try {
      List<String> fascicoliElaborati = new ArrayList<>();
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.YEAR, -1);
      Date date = calendar.getTime();
      String afterDate = UtilityDate.convertiData(date, UtilityDate.FORMATO_GG_MM_AAAA);
      String beforeDate = UtilityDate.convertiData(new Date(), UtilityDate.FORMATO_GG_MM_AAAA);
      List fascicoli = sqlManager.getListVector("select ws.entita, ws.key1, vgg.codgar, ws.codice, ws.anno, ws.numero, ws.classifica from wsfascicolo ws left join v_gare_genere vgg on vgg.codice=ws.key1 " +
              "where ws.entita in ('GARE','TORN')", new Object[]{}); //non gestita la stipula,'G1STIPULA'

      String query = "select count(*) from  gare g join V_GARE_GENERE vgg on g.codgar1=vgg.codgar and vgg.genere <100 left join V_GARE_STATOESITO v on (v.codgar = g.codgar1 and codiga is null) "
              + " left join torn t on t.codgar = v.codgar and vgg.genere in (1,2,3) left join pubbli p on p.codgar9=g.codgar1  and p.tippub=11 and (vgg.genere=11 or vgg.genere=10 or vgg.genere=20) left join garealbo ga on ga.codgar=g.codgar1 and vgg.genere=10 "
              + " where ("//condizioni per gare
              + "(vgg.genere in (1,2,3) and v.codstato = 3 and ("
              + "   (g.dattoa > " + sqlManager.getDBFunction("stringtodate", new String[]{afterDate}) + " and g.dattoa < " + sqlManager.getDBFunction("stringtodate", new String[]{beforeDate}) + ") "
              + " or (g.datneg > " + sqlManager.getDBFunction("stringtodate", new String[]{afterDate}) + " and g.datneg < " + sqlManager.getDBFunction("stringtodate", new String[]{beforeDate}) + ") "
              + " or (t.datneg > " + sqlManager.getDBFunction("stringtodate", new String[]{afterDate}) + " and t.datneg < " + sqlManager.getDBFunction("stringtodate", new String[]{beforeDate}) + ") "
              + " or ((t.iterga <> 10 or t.iterga is null) and g.datconclusa > " + sqlManager.getDBFunction("stringtodate", new String[]{afterDate}) + " and g.datconclusa < " + sqlManager.getDBFunction("stringtodate", new String[]{beforeDate}) + ")"
              + ") or (g.dattoa is null and g.datneg is null and t.datneg is null and g.datconclusa is null) ) or ("
              //condizione per gli avvisi
              + "vgg.genere=11 and (p.tippub=11 and (p.datpub > " + sqlManager.getDBFunction("stringtodate", new String[]{afterDate}) + " and p.datpub < " + sqlManager.getDBFunction("stringtodate", new String[]{beforeDate}) + ") "
              + ") or (p.tippub is null and p.datpub is null)) or ("
              //condizione per gli elenchi
              + " (vgg.genere=10 or vgg.genere=20) and p.tippub=11 and (ga.dtermval is NULL or (ga.dtermval > " + sqlManager.getDBFunction("stringtodate", new String[]{afterDate}) + " and ga.dtermval < " + sqlManager.getDBFunction("stringtodate", new String[]{beforeDate}) + ")"
              + "))) and vgg.codice=?";
      String codicefascicolo = null;
      for (Object fascicolo : fascicoli) {
        try {
          codicefascicolo = SqlManager.getValueFromVectorParam(fascicolo, 3).stringValue();
          if (!fascicoliElaborati.contains(codicefascicolo)) {
            fascicoliElaborati.add(codicefascicolo);

            String entita = SqlManager.getValueFromVectorParam(fascicolo, 0).stringValue();
            String key1 = SqlManager.getValueFromVectorParam(fascicolo, 1).stringValue();
            Long anno = SqlManager.getValueFromVectorParam(fascicolo, 4).longValue();
            String numero = SqlManager.getValueFromVectorParam(fascicolo, 5).stringValue();
            String classifica = SqlManager.getValueFromVectorParam(fascicolo, 6).stringValue();
            Long idconfiL;
            if ("G1STIPULA".equals(entita)) {
              idconfiL = gestioneWSDMManager.getWsdmConfigurazioneFromIdStipula(new Long(key1), "PG");
              query = "select count(*) from v_gare_stipula where stato in (5,6)  and "
                      + "((daatto > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and daatto < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") " +
                      "or daatto is null) and id=?";
            } else {
              String codgar = SqlManager.getValueFromVectorParam(fascicolo, 2).stringValue();
              idconfiL = gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(codgar, "PG");
            }
            String idconfi = Long.toString(idconfiL);
            boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
            if (isIntegrazioneWSDMAttivaValida) {
              WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
              if (configurazione.isEsito()) {
                String tipoWSDM = configurazione.getRemotewsdm();
                if ("LAPISOPERA".equals(tipoWSDM)) {
                  String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE + idconfi);
                  if ("1".equals(wsdmLoginComune)) {
                    String[] datiLoginComune = gestioneWSDMManager.getWSDMLoginComune(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
                    String username = datiLoginComune[0];
                    String password = datiLoginComune[1];
                    String ruolo = datiLoginComune[2];
                    String nome = datiLoginComune[3];
                    String cognome = datiLoginComune[4];
                    String codiceuo = datiLoginComune[5];
                    String idutente = datiLoginComune[6];
                    String idutenteunop = datiLoginComune[7];

                    Long count = (Long) sqlManager.getObject(query, new Object[]{key1});
                    if (count > 0) {
                      WSDMFascicoloResType wsdmFascicoloRes = this.gestioneWSDMManager.wsdmFascicoloMetadatiLeggi(username, password, ruolo, nome, cognome, codiceuo,
                              idutente, idutenteunop, codicefascicolo, anno, numero, GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, classifica, idconfi);

                      if (wsdmFascicoloRes.isEsito()) {
                        if (wsdmFascicoloRes.getFascicolo() != null) {
                          try {
                            this.gestioneWSDMManager.setWSFascicolo(entita, key1, "", "", "", wsdmFascicoloRes.getFascicolo().getCodiceFascicolo(), wsdmFascicoloRes.getFascicolo().getAnnoFascicolo(), wsdmFascicoloRes.getFascicolo().getNumeroFascicolo(),
                                    wsdmFascicoloRes.getFascicolo().getClassificaFascicolo(), "", "",
                                    wsdmFascicoloRes.getFascicolo().getStruttura(), null, wsdmFascicoloRes.getFascicolo().getClassificaFascicoloDescrizione(), "", "", "");
                            errMsgs.add(key1 +" (fascicolo: " +codicefascicolo + ") - esito positivo");
                          } catch (Exception e) {
                            if(logger.isDebugEnabled()) logger.debug("Errore durante la lettura dei dati fascicoli", e);
                            errMsgs.add(key1 +" (fascicolo: " +codicefascicolo +") - esito negativo");
                          }
                        }
                      } else {
                        if(logger.isDebugEnabled()) logger.debug("Errore durante la lettura dei dati fascicoli: "+ wsdmFascicoloRes.getMessaggio());
                        errMsgs.add(key1 +" (fascicolo: " +codicefascicolo + ") - esito negativo");
                      }
                    }
                  }
                }
              }
            }
          }
        } catch (SQLException e) {
          errMsgs.add("Errore SQL durante la lettura dei dati del fascicolo: " + codicefascicolo);
        }
      }
    } catch (Exception e){
      logger.error("Errore inaspettato durante la lettura dei dati fascicoli", e);
      livEvento=3;
    } finally {
      if (!errMsgs.isEmpty()) {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setCodEvento("GA_WSDM_LETTURA_FASCICOLI");
          logEvento.setDescr("Esecuzione task di lettura dati fascicolo");
          logEvento.setErrmsg(String.join("\n", errMsgs));
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
    }
  }
}