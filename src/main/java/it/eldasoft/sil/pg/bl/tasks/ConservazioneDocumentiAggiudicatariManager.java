/*
 * Created on 28/03/23
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.dm.WSDMDocumentoAggiungiInformazioniResType;

public class ConservazioneDocumentiAggiudicatariManager {

  static Logger            logger                 = Logger.getLogger(ConservazioneDocumentiAggiudicatariManager.class);

  private SqlManager       sqlManager;

  private GestioneWSDMManager gestioneWSDMManager;

  private GenChiaviManager genChiaviManager;

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param gestioneWSDMManager the gestioneWSDMManager to set
   */
  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  /**
   * @param genChiaviManager the genChiaviManager to set
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public static List getDocumentiDaGestire(SqlManager sqlManager, String codgar, boolean soloAggiudicatari) {
    if(logger.isDebugEnabled()) logger.debug("getDocumentiDaGestire: inizio metodo");

    List<?> listaDocumenti = null;

    String queryDocRiepilogoBuste = "select distinct CASE WHEN wd.numerodoc IS not null THEN wd.numerodoc ELSE wsd.numerodoc END AS NUMERODOC "+
        " from ditg left outer join ragimp on (codime9=dittao and impman='1')"+
        " inner join w_puser on (userkey1=dittao or (userkey1=coddic and impman='1'))"+
        " inner join w_invcom i on (comkey1=usernome and comkey2=ngara5 and (comkey3 = ncomope or comkey3 is null))"+
        " join w_docdig w on "+sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"})+" = w.digkey1 and w.digent='W_INVCOM'"+
        " left join wsdocumento wd on i.comnumprot=wd.numeroprot"+
        " left join wsallegati wa on wa.key2="+sqlManager.getDBFunction("inttostr",  new String[] {"w.IDDOCDIG"})+" and wa.key1=w.idprg and wa.entita='W_DOCDIG' left join wsdocumento wsd on wa.idwsdoc = wsd.id"+
        " where ((COMTIPO in('FS10','FS11') and upper(DIGNOMDOC) LIKE 'RIEPILOGO_BUSTE.PDF%') or COMTIPO='FS14') and COMSTATO='6' and ditg.codgar5 = ? "+
        " and (CASE WHEN COMTIPO='FS10' THEN DRICIND WHEN COMTIPO='FS11' THEN DATOFF ELSE DATRINUNCIA END is not null) and (wd.numerodoc is not null or wsd.numerodoc is not null) ";

    if(soloAggiudicatari)
      queryDocRiepilogoBuste +=" and (dittao in (select ditta from gare gy where gy.codgar1=ditg.codgar5) "
          + "or dittao in (select dittao from gare gx join ditgaq dd on gx.ngara=dd.ngara and gx.codgar1=ditg.codgar5))";

    String queryDocDitta = "select distinct wd.numerodoc "+
        " from v_gare_docditta d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg "+
        " join imprdocg i on d.codgar = i.codgar and d.ngara = i.ngara and d.codimp = i.codimp and d.norddoci = i.norddoci and d.proveni = i.proveni "+
        " join gare g on d.ngara=g.ngara "+
        " left join ditgaq dq on dq.ngara = d.ngara and dq.dittao=d.codimp"+
        " join wsallegati wa on wa.key2="+sqlManager.getDBFunction("inttostr",  new String[] {"w.IDDOCDIG"})+" and wa.key1=d.idprg and wa.entita='W_DOCDIG' join wsdocumento wd on wa.idwsdoc = wd.id"+
        " where d.codgar = ? and d.idprg is not null and d.iddocdg is not null ";
    if(soloAggiudicatari)
      queryDocDitta += "and ((d.busta=3 or d.busta= 2) and (dq.dittao=d.codimp or g.ditta=d.codimp and dq.dittao is null)"
          + " or (d.busta=1 and (d.codimp in (select dittao from gare gx join ditgaq dd on gx.ngara=dd.ngara and gx.codgar1=d.codgar) or"
          + " d.codimp in (select ditta from gare gy where gy.codgar1=d.codgar))))";

    String queryDocComDallaDitta = "select distinct wd.numerodoc "+
        " from w_invcom i join w_docdig w on "+sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) +" = w.digkey1 and i.idprg=w.idprg and w.digent='W_INVCOM' "+
        " join gare g on i.comkey2=g.ngara "+
        " join wsallegati wa on wa.key2="+sqlManager.getDBFunction("inttostr",  new String[] {"w.IDDOCDIG"})+" and wa.key1=w.idprg and wa.entita='W_DOCDIG' left join wsdocumento wd on wa.idwsdoc = wd.id "+
        " where i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT IS NULL and g.codgar1=? ";
    if(soloAggiudicatari)
      queryDocComDallaDitta += " and i.comkey1 in(select usernome from w_puser where userkey1 in (select ditta from gare gy where gy.codgar1=g.codgar1) "+
          " or userkey1 in (select dittao from gare gx join ditgaq dd on gx.ngara=dd.ngara and gx.codgar1=g.codgar1) or userkey1 in (select coddic from RAGIMP where impman='1' and codime9=g.ditta))";

    String queryDocumenti = queryDocRiepilogoBuste + " union " + queryDocDitta + " union " + queryDocComDallaDitta;

    try {
      listaDocumenti = sqlManager.getListVector(queryDocumenti, new Object[]{codgar,codgar,codgar});
      return listaDocumenti;

    }catch(SQLException e) {
      logger.error("Errore durante la selezione dei documenti della gara da gestire " + e.getMessage());
    }

    if(logger.isDebugEnabled()) logger.debug("getDocumentiDaGestire: fine metodo");

    return null;
  }

  public void aggiungiInformazioniDocumentoWSDM()  {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato
    if (WebUtilities.isAppNotReady()) return;

    if(logger.isDebugEnabled()) logger.debug("aggiungiInformazioniDocumentoWSDM: inizio metodo");

    String getJobsSql="select id_archiviazione, sysdet, codgara from gardoc_jobs where da_processare = '1' and tipo_archiviazione = 4 order by id_archiviazione";
    try {
      List<?> jobs = this.sqlManager.getListVector(getJobsSql, null);

      if(jobs!=null && jobs.size()>0) {
        for(int i=0;i<jobs.size();i++) {
          String errori="";
          Long id_archiviazione= SqlManager.getValueFromVectorParam(jobs.get(i), 0).longValue();
          Long sysdet= SqlManager.getValueFromVectorParam(jobs.get(i), 1).longValue();
          String codgar= SqlManager.getValueFromVectorParam(jobs.get(i), 2).getStringValue();

          String username = null;
          String password = null;

          Long idconfi = this.gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(codgar, "PG");

          String selectWslogin = "select l.username, l.password from wslogin l" +
              " where idconfiwsdm = ? and servizio= ?";



          if(idconfi!=null){
            Vector<?> datiWslogin = sqlManager.getVector(selectWslogin, new Object[] {idconfi, "DOCUMENTALE" });
            if (datiWslogin != null && datiWslogin.size() > 0) {
              username = (String) ((JdbcParametro) datiWslogin.get(0)).getValue();
              password = (String) ((JdbcParametro) datiWslogin.get(1)).getValue();

              String passwordDecoded = null;
              if (password != null && password.trim().length() > 0) {
                ICriptazioneByte passwordICriptazioneByte = null;
                try {
                  passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
                      ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
                      ICriptazioneByte.FORMATO_DATO_CIFRATO);
                } catch (CriptazioneException e) {
                  ;//messaggio di eccezione
                }
                passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
              }else{
                passwordDecoded = "";
              }


              //Prima iterazione
              boolean aggiudicatari=false;
              List<?> documentiDaResettare = getDocumentiDaGestire(this.sqlManager, codgar, aggiudicatari);
              if((documentiDaResettare!=null && documentiDaResettare.size()>0)) {
                for(int j=0; j<documentiDaResettare.size();j++) {
                  String numerodoc = SqlManager.getValueFromVectorParam(documentiDaResettare.get(j), 0).getStringValue();
                  //chiamata al wsdm
                  try {
                    WSDMDocumentoAggiungiInformazioniResType res = gestioneWSDMManager.WSDMDocumentoAggiungiInformazioni(numerodoc, aggiudicatari, username,passwordDecoded,"DOCUMENTALE",idconfi);
                    if(!res.isEsito()) {
                      errori+="\r\nDati non aggiornati per il documento con numerodoc="+numerodoc +" (aggiudicatario=no)";
                      if(res.getMessaggio()!=null)
                        errori+=": " + res.getMessaggio();
                    }
                  }catch(GestoreException e) {
                    logger.error(e);
                    errori+="\r\nErrore di elaborazione dati per il documento con numerodoc="+numerodoc +": " + e.getMessage();
                  }
                }
              }
              //Seconda iterazione
              aggiudicatari=true;
              List<?> documentiDaMarcare = getDocumentiDaGestire(this.sqlManager, codgar, aggiudicatari);
              if((documentiDaMarcare!=null && documentiDaMarcare.size()>0)) {
                for(int j=0; j<documentiDaMarcare.size();j++) {
                  String numerodoc = SqlManager.getValueFromVectorParam(documentiDaMarcare.get(j), 0).getStringValue();
                  //chiamata al wsdm
                  try {
                    WSDMDocumentoAggiungiInformazioniResType res = gestioneWSDMManager.WSDMDocumentoAggiungiInformazioni(numerodoc, aggiudicatari, username,passwordDecoded,"DOCUMENTALE",idconfi);
                    if(!res.isEsito()) {
                      errori+="\r\nDati non aggiornati per il documento con numerodoc="+numerodoc +" (aggiudicatario=si)";
                      if(res.getMessaggio()!=null)
                        errori+=": " + res.getMessaggio();
                    }
                  }catch(GestoreException e) {
                    logger.error(e);
                    errori+="\r\nErrore di elaborazione dati per il documento con numerodoc="+numerodoc +": " + e.getMessage();
                  }
                }
              }
            }else {
              errori+="\r\nIl wsdm non risulta configurato correttamente. Idconfi="+idconfi;
            }
          }else {
            errori+="\r\nNon è stata trovata la configurazione al wsdm. Idconfi="+idconfi;
          }
          updateJob(sysdet, codgar, id_archiviazione, errori);

        }
      }
    }catch(SQLException e) {
      logger.error("Errore nella lettura della gardoc_jobs durante l'esecuzione del task \"aggiungiInformazioniDocumentoWSDM\"");
    }catch(GestoreException e) {
      logger.error("Errore nella lettura della gardoc_jobs durante l'esecuzione del task \"aggiungiInformazioniDocumentoWSDM\"");
    }

    if(logger.isDebugEnabled()) logger.debug("aggiungiInformazioniDocumentoWSDM: fine metodo");
  }

  private void updateJob(Long sysdet, String codgar, Long id_archiviazione, String errori) {
    try {
      String codgarApp = codgar;
      if (codgar.startsWith("$")) {
        codgarApp = codgar.substring(1);
      }

      String message = "La richiesta di conservazione documenti aggiudicatario per la gara "+codgarApp+" è stata elaborata con esito positivo";
      if(!"".equals(errori)) {
        message = "La richiesta di conservazione documenti aggiudicatario per la gara "+codgarApp+" ha generato degli errori"; 

      }

      this.sqlManager.update("update gardoc_jobs set da_processare = '2', data_creazione = ?, esito = ? where id_archiviazione = ?", new Object[]{new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), message+errori, id_archiviazione});
      //inserisco il messaggio
      String insertW_MESSAGE_IN = "insert into w_message_in (message_id, message_date, message_subject, message_body, message_sender_syscon, message_recipient_syscon, message_recipient_read) values (?,?,?,?,?,?,?)";
      Long maxMessageIdIn = (Long) this.sqlManager.getObject("select max(message_id) from w_message_in", new Object[] {});
      if (maxMessageIdIn == null) maxMessageIdIn = new Long(0);
      maxMessageIdIn = new Long(maxMessageIdIn.longValue() + 1);
      this.sqlManager.update(insertW_MESSAGE_IN, new Object[] { maxMessageIdIn, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
          message, null, 50, sysdet, new Long(0) });
    } catch (Exception e) {
      logger.error("aggiungiInformazioniDocumentoWSDM.scriviMessaggio : impossibile scrivere il messaggio di avvenuta conservazione dei documenti degli aggiudicatari", e);
    }
  }

  /**
   * Il metodo restituisce l'id dell'occorrenza inserita in gardoc_jobs
   * @param syscon
   * @param codgar
   * param entita
   * @return Long
   * @throws Exception
   */
  public Long insertJob(Long syscon, String codgar, String entita, Long sysdet) throws Exception{

    Long id_archiviazione = null;
    try {
      id_archiviazione = new Long(genChiaviManager.getNextId("GARDOC_JOBS"));

      //aggiorno flag di esecuzione nella lista record da elaborare
      this.sqlManager.update("insert into gardoc_jobs(id_archiviazione, syscon, codgara, data_inserimento, da_processare, tipo_archiviazione, entita, sysdet) "
          + "values(?,?,?,?,'1',?,?,?)", new Object[]{id_archiviazione, syscon, codgar, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),new Long(4), entita, sysdet });

    } catch (Exception e) {
      logger.error("aggiungiInformazioniDocumentoWSDM.insertJob : errore durante l'inserimento del job di marcatura dei documenti aggiudicatario per la gara " + codgar, e);
      throw e;
    }
    return id_archiviazione;
  }

}
