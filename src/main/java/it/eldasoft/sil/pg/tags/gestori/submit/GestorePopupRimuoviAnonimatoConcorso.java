/*
 * Created on 21/09/2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneType;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.spring.UtilitySpring;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire rimuovere
 * l'anomizzazione dei concorrenti nei concorsi di progettazione.
 *
 * @author Manuel Bridda
 */
public class GestorePopupRimuoviAnonimatoConcorso extends AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupRimuoviAnonimatoConcorso.class);

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private MEPAManager mepaManager = null;

  private PgManager pgManager = null;

  private String selectW_INVCOM = "select idprg, idcom, comkey1, comkey2 from w_invcom where comkey2 = ? and comstato = '6' and comtipo = 'FS11' order by comkey1, comkey3";

  private String selectW_DOCDIG = "select idprg, iddocdig from w_docdig where digent = ? and idprg = ? and digkey1 = ? and lower(dignomdoc) = '" + CostantiAppalti.nomeFileDatiPartecipazione + "'";
  private String selectW_PUSER = "select userkey1 from w_puser where usernome = ?";

  public GestorePopupRimuoviAnonimatoConcorso() {
    super(false);
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
        this.getServletContext(), MEPAManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");

    String ngarapa=null;
    String errMsgEvento=null;
    int countSuccess1g=0;
    int countError1g=0;
    int countSuccess2g=0;
    int countError2g=0;
    String codimp=null;
    String comkey1=null;
    String comkey2=null;
    Long w_invcom_idcom=null;


    List<?> datiW_INVCOM = null;
    try {
      datiW_INVCOM = this.sqlManager.getListVector(selectW_INVCOM, new Object[] { ngara });
      ngarapa = (String) this.sqlManager.getObject("select ngara from gare where ngarapa=?", new Object[] { ngara });
      if(StringUtils.isNotBlank(ngarapa))//se esiste un concorso di secondo grado, dobbiamo gestire anche quello
        datiW_INVCOM.addAll(this.sqlManager.getListVector(selectW_INVCOM, new Object[] { ngarapa }));
    }catch(SQLException le) {
      String messageKey = "errors.logEventi.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), le);
    }

    if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(1);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento("GA_ACQUISIZIONE_OFFCONC_INIT");
        logEvento.setDescr("Inizio acquisizione offerte da portale Appalti per rimozione anonimato concorso di progettazione");
        logEvento.setErrmsg(null);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
      for(int i=0; i<datiW_INVCOM.size(); i++) {
        boolean acquisito=false;
        codimp=null;
        comkey1=null;
        w_invcom_idcom=null;
        comkey2=null;
        try {
          String w_invcom_idprg = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 0).getValue();
          w_invcom_idcom = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 1).getValue();
          comkey1 = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 2).getValue();
          comkey2 = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 3).getValue();
          String userkey1 = (String) this.sqlManager.getObject(selectW_PUSER, new Object[] { comkey1 });
          String idanonimo=pgManager.codifica(ngara+"#"+userkey1);
          String dittao= (String) sqlManager.getObject("select dittao from DITG where ngara5=? and idanonimo=? and rtofferta is null", new Object[] {comkey2,idanonimo});
          String isRT = (String) sqlManager.getObject("select isrt from g1imprconc where codimp=?", new Object[] {dittao});
          if(StringUtils.isNotBlank(dittao)) {
            try {
              status = this.sqlManager.startTransaction();
              codimp = userkey1;
              errMsgEvento="";

              TipoPartecipazioneDocument document = null;
              document = mepaManager.estrazioneDocumentMessaggioFS10_FS11(selectW_DOCDIG, w_invcom_idprg,
                  w_invcom_idcom.toString());

              //faccio gli inserimenti nella IMPR che non erano stati fatti in fase di acquisizione
              if (document != null) {   
                boolean gestioneComponenti=false;
                String codiceRaggruppamento = "";
                String ragioneSociale = (String) sqlManager.getObject("select nomest from impr where codimp=?", new Object[] { codimp });
                if (ragioneSociale.length() > 61) 
                  ragioneSociale = ragioneSociale.substring(0, 60);
                if (document.getTipoPartecipazione().getRti()){
                  if(!comkey2.equals(ngara) && "1".equals(isRT)) {//avere isrt=1 vuol dire che la ditta era una RT nel primo grado! ergo devo recuperarmi i dati e non crearne una nuova
                    Vector<?> datiRt= this.sqlManager.getVector("select dittao,nomimo from ditg where ngara5=? and dittao in (select codime9 from ragimp where impman='1' and coddic=?)", new Object[] {ngara,userkey1});
                    if(datiRt!=null && datiRt.size()>0){
                      codimp = userkey1 = SqlManager.getValueFromVectorParam(datiRt, 0).stringValue();
                      ragioneSociale = SqlManager.getValueFromVectorParam(datiRt, 1).stringValue();
                    }else {//se entro qui, non ho trovato l'rt che cercavo. Probabilmente non è stata ancora decodificata (errore?)
                      logger.error("Ditta RT del primo grado non ancora decodificata");
                      throw new Exception("Ditta RT del primo grado non ancora decodificata");
                    }
                  }else {
                    // Inserimento in anagrafica della RTI
                    String nomeRTI = document.getTipoPartecipazione().getDenominazioneRti();
                    Double quotaMandataria = null;
                    if (document.getTipoPartecipazione().isSetQuotaMandataria())
                      quotaMandataria = new Double(document.getTipoPartecipazione().getQuotaMandataria());
                    codiceRaggruppamento = geneManager.calcolaCodificaAutomatica("IMPR", "CODIMP");
                    String nomeRTITroncato = nomeRTI;
                    if (nomeRTITroncato.length() > 61) 
                      nomeRTITroncato = nomeRTITroncato.substring(0, 60);
                    Long tipimp = (Long) sqlManager.getObject("select tipimp from impr where codimp=?", new Object[] { codimp });
                    Long tipimpNewRTI = null;
                    if (tipimp != null && tipimp.longValue() <= 5)
                      tipimpNewRTI = new Long(3);
                    else if (tipimp != null && tipimp.longValue() > 5) tipimpNewRTI = new Long(10);
                    sqlManager.update("insert into IMPR (CODIMP,NOMIMP,NOMEST,TIPIMP) values(?,?,?,?)", new Object[] { codiceRaggruppamento,
                        nomeRTITroncato, nomeRTI, new Long(tipimpNewRTI) });
                    sqlManager.update("insert into RAGIMP (IMPMAN,CODDIC,NOMDIC,CODIME9,QUODIC) values(?,?,?,?,?)", new Object[] { "1",
                        userkey1, ragioneSociale, codiceRaggruppamento, new Double(quotaMandataria) });
                    gestioneComponenti = true;
                    codimp = codiceRaggruppamento;
                    ragioneSociale = nomeRTITroncato;
                    }
                  //andiamo ad aggiornare il campo RTOFFERTA se questo era valorizzato
                  sqlManager.update("update ditg set rtofferta=? where rtofferta is not null and ngara5=? and idanonimo=?", new Object[] {codimp,comkey2,idanonimo});
                  //andiamo ad aggiornare il campo DITTAINV se questo era valorizzato
                  sqlManager.update("update ditg set dittainv=? where dittainv is not null and ngara5=? and idanonimo=?", new Object[] {userkey1,comkey2,idanonimo});
                  //aggiorniamo il record di ditg che 
                } else {
                  // Si controlla se l'impresa è un consorzio
                  Long tipoImpresa = (Long) this.sqlManager.getObject("select tipimp from impr where codimp=?", new Object[] { codimp });
                  if (tipoImpresa != null && (tipoImpresa.longValue() == 2 || tipoImpresa.longValue() == 11)) {
                    gestioneComponenti = true;
                    codiceRaggruppamento = userkey1;
                  }
                }
                // Inserimento delle componenti del consorzio/RTI
                ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
                TipoPartecipazioneType tipoPartecipazione = document.getTipoPartecipazione();
                if (gestioneComponenti) {
                  // Si controlla se vi sono partecipanti
                  if (tipoPartecipazione.isSetPartecipantiRaggruppamento()) {
                    Long tipoImpresa = new Long(2);
                    if (tipoPartecipazione.isSetDenominazioneRti()) tipoImpresa = new Long(3);
                    pgManager.gestionePartecipanti(tipoPartecipazione.getPartecipantiRaggruppamento(), codiceRaggruppamento, tipoImpresa,
                        profilo.getId(), comkey2, codimp);
                  }
                }
                aggiornaOccorrenze(ragioneSociale,codimp,dittao,comkey2,userkey1,comkey1,idanonimo);
                
                acquisito=true;
                if(comkey2.equals(ngara))
                countSuccess1g++;
                else
                  countSuccess2g++;
              }else {
                errMsgEvento="Nessun documento associato";
              }
            }catch(Exception e) {
              if(comkey2.equals(ngara))
                countError1g++;
               else
                countError2g++;
              errMsgEvento+=e.getMessage()+"\n";
              logger.error(e);
            }
            finally {
              try {
                if(acquisito)
                  this.sqlManager.commitTransaction(status);
                else
                  this.sqlManager.rollbackTransaction(status);
              }catch(SQLException e) {
                logger.error(e);
              }
              LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
              logEvento.setLivEvento(acquisito?1:3);
              logEvento.setOggEvento(ngara);
              logEvento.setCodEvento("GA_ACQUISIZIONE_OFFCONC");
              logEvento.setDescr("Acquisizione offerta ditta "+dittao+" per rimozione anonimato concorso di progettazione (cod. anagrafica "+codimp+" - cod.operatore "+comkey1+" - id.comunicazione "+w_invcom_idcom+")");
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            }
          }//fine if per controllo dittao da aggiornare
        }catch(SQLException | CriptazioneException e) { //qui non dovrebbe mai entrare
          logger.error("Errore in fase di recupero dei dati ",e);
        }
      }//fine ciclo for
      
      //aggiorniamo i record che non hanno presentato offerta (solo secondo grado)
      List datiDITG = null;
      try {
        if(StringUtils.isNotBlank(ngarapa))
          datiDITG = this.sqlManager.getListVector("select dittao,idanonimo from ditg where idanonimo is not null and invoff = '2' and ngara5=?", new Object[] {ngarapa});
        if(datiDITG!= null && datiDITG.size()>0) {
          status = this.sqlManager.startTransaction();
          for(int i=0;i<datiDITG.size();i++) {
            String dittao = (String) SqlManager.getValueFromVectorParam(datiDITG.get(i), 0).getValue();
            String idanonimo = (String) SqlManager.getValueFromVectorParam(datiDITG.get(i), 1).getValue();
            String iddecodificato=this.pgManager.decodifica(idanonimo);
            codimp = null;
            if(iddecodificato.indexOf("#")>-1) {
              codimp=this.pgManager.decodifica(iddecodificato).split("#")[1];
              String ragioneSociale = null;
              String isRT = (String) sqlManager.getObject("select isrt from g1imprconc where codimp=?", new Object[] {dittao});
              if("1".equals(isRT)) {//se la ditta che rinuncia era un rt nel primo grado, devo recuperare il dittao/nomimo decodificato dal concorso di 1 grado
                Vector<?> datiRt= this.sqlManager.getVector("select dittao,nomimo from ditg where ngara5=? and dittao in (select codime9 from ragimp where impman='1' and coddic=?)", new Object[] {ngara,codimp});
                if(datiRt!=null && datiRt.size()>0){
                  codimp = SqlManager.getValueFromVectorParam(datiRt, 0).stringValue();
                  ragioneSociale = SqlManager.getValueFromVectorParam(datiRt, 1).stringValue();
                }else {//se entro qui, non ho trovato l'rt che cercavo. Probabilmente non è stata ancora decodificata (errore?)
                  logger.error("Ditta RT del primo grado non ancora decodificata");
                  throw new Exception("Ditta RT del primo grado non ancora decodificata");
                }
              }else {
                ragioneSociale= (String) this.sqlManager.getObject("select nomimp from impr where codimp=?", new Object[] {codimp});
              }
              String usernome= (String) this.sqlManager.getObject("select usernome from w_puser where userkey1=? and userent='G1IMPRCONC'", new Object[] {dittao}); 
              usernome=this.pgManager.decodifica(usernome);
              aggiornaOccorrenze(ragioneSociale,codimp,dittao,ngarapa,codimp,usernome,idanonimo);
              countSuccess2g++;
            }else {
              countError2g++;
              errMsgEvento+="Id anonimo non corretto per"+dittao+"\n";
              logger.error(errMsgEvento);
            }
          }
          this.sqlManager.commitTransaction(status);
        }
      }catch(Exception e) {
        countError2g++;
        errMsgEvento+=e.getMessage()+"\n";
        logger.error(e);
        try {
          this.sqlManager.rollbackTransaction(status);
        } catch (SQLException e1) {
          logger.error(e1);
        }
      }
      this.getRequest().setAttribute("successi1g", countSuccess1g);
      this.getRequest().setAttribute("errori1g", countError1g);
      this.getRequest().setAttribute("successi2g", countSuccess2g);
      this.getRequest().setAttribute("errori2g", countError2g);
      logger.info("Acquisizione offerte da portale Appalti, numero acquisizioni offerte primo grado: " + countSuccess1g);
      logger.info("Acquisizione offerte da portale Appalti, numero acquisizioni offerte primo grado con errore: " + countError1g);
      logger.info("Acquisizione offerte da portale Appalti, numero acquisizioni offerte secondo grado: " + countSuccess2g);
      logger.info("Acquisizione offerte da portale Appalti, numero acquisizioni offerte secondo grado con errore: " + countError2g);
      Long ditteDaGestire = 0L;
      try {
        ditteDaGestire = (Long) this.sqlManager.getObject("select count(1) from ditg where idanonimo is not null and ngara5=? or ngara5=", new Object[] {ngara,ngarapa});
      } catch (SQLException e) {
      }
      if(new Long(0).equals(ditteDaGestire)) {
        this.getRequest().setAttribute("operazioneEseguita", "2");
        try {
          sqlManager.update("update TORN set ISFINECONC='1' where codgar=? or codgar=?", new Object[] {"$"+ngara,"$"+ngarapa});
        } catch (SQLException le) {
          String messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }else
        this.getRequest().setAttribute("operazioneEseguita", "1");
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(1);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento("GA_ACQUISIZIONE_OFFCONC_FINE");
        logEvento.setDescr("Fine acquisizione offerte da portale Appalti per rimozione anonimato concorso di progettazione: " +
            countSuccess1g + " acquisizioni offerte per concorso di primo grado, " +
            countError1g + " acquisizioni offerte con errore per concorso di primo grado" +
            (StringUtils.isNotBlank(ngarapa)? 
                 ", "+countSuccess2g +" acquisizioni offerte per concorso di secondo grado, " +
                countError2g + " acquisizioni offerte con errore per concorso di secondo grado" : "")
            );
        logEvento.setErrmsg(null);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }
  
  
  private void aggiornaOccorrenze(String ragioneSociale,String codimp,String dittao, String ngara, String userkey1, String comkey1, String idanonimo) throws SQLException {
    
    Vector<?> backupDitgEventi = null;
    if("MSQ".equals(SqlManager.getTipoDB())) {
      //Passaggi aggiuntivi per APPALTI MSQ perché non ha possibilità di valutare la FK dopo il commit. (problemi su FK_DITGEVENTI_DITG)
      backupDitgEventi = sqlManager.getVector("select codgar,"
      + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "datfs10" })+","
      + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "datfs11" })+","
      + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "datfs11a" })+","
      + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "datfs11b" })+","
      + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "datfs11c" })+","
      + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "datfs11b1" })+","
      + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "datfs14" })
      + " from DITGEVENTI where ngara=? and dittao=?", new Object[] {ngara, dittao});
      //elimino il record in db per permettere l'update del dittao su DITG
      sqlManager.update("delete from DITGEVENTI where ngara=? and dittao=?", new Object[] {ngara, dittao});
    }
    //aggiornamento della DITG
    sqlManager.update("update DITG set nomimo=?, dittao=?, idanonimo=? WHERE dittao=? and ngara5=?", 
        new Object[] {ragioneSociale,codimp,null,dittao,ngara});
    //aggiornamento della EDIT 
    sqlManager.update("update EDIT set nomime=?, codime=? WHERE codime=? and codgar4=?",
        new Object[] {ragioneSociale,codimp,dittao,"$"+ngara});
    //aggiornamento della IMPRDOCG 
    sqlManager.update("update IMPRDOCG set codimp=? WHERE codimp=? and ngara=?",
        new Object[] {codimp,dittao,ngara});
    
    if("MSQ".equals(SqlManager.getTipoDB()) && backupDitgEventi!=null && backupDitgEventi.size()>0) {//reinserisco il record in DITGEVENTI con dittao aggiornato per appalti msq
      sqlManager.update("insert into DITGEVENTI(codgar,ngara,dittao,datfs10,datfs11,datfs11a, datfs11b, datfs11c,datfs11b1,datfs14) values (?,?,?,?,?,?,?,?,?,?)",
          new Object[] {SqlManager.getValueFromVectorParam(backupDitgEventi, 0).getValue(),
              ngara,
              codimp,
              SqlManager.getValueFromVectorParam(backupDitgEventi, 1).getValue(),
              SqlManager.getValueFromVectorParam(backupDitgEventi, 2).getValue(),
              SqlManager.getValueFromVectorParam(backupDitgEventi, 3).getValue(),
              SqlManager.getValueFromVectorParam(backupDitgEventi, 4).getValue(),
              SqlManager.getValueFromVectorParam(backupDitgEventi, 5).getValue(),
              SqlManager.getValueFromVectorParam(backupDitgEventi, 6).getValue(),
              SqlManager.getValueFromVectorParam(backupDitgEventi, 7).getValue()
              });
    }else {
    //Aggiornamento di DITGEVENTI
    sqlManager.update("update DITGEVENTI set dittao=? where dittao=? and ngara=?",
        new Object[] {codimp,dittao,ngara});
    }
    
    //Aggiornamento di DITGAMMIS
    sqlManager.update("update DITGAMMIS set dittao=? where dittao=? and ngara=?",
        new Object[] {codimp,dittao,ngara});
    //Aggiornamento di DITGSTATI
    sqlManager.update("update DITGSTATI set dittao=? where dittao=? and ngara=?",
        new Object[] {codimp,dittao,ngara});
    //Aggiornamento di DPUN
    sqlManager.update("update DPUN set dittao=? where dittao=? and ngara=?",
        new Object[] {codimp,dittao,ngara});
    //Aggiornamento di DPUN_CG
    sqlManager.update("update DPUN_CG set dittao=? where dittao=? and ngara=?",
        new Object[] {codimp,dittao,ngara});
    //Aggiornamento di G1CRIVAL
    sqlManager.update("update G1CRIVAL set dittao=? where dittao=? and ngara=?",
        new Object[] {codimp,dittao,ngara});
    //Aggiornamento di G1CRIVAL_CG
    sqlManager.update("update G1CRIVAL_CG set dittao=? where dittao=? and ngara=?",
        new Object[] {codimp,dittao,ngara});
    //Aggiornamento di GARE
    sqlManager.update("update GARE set dittap=? where dittap=? and ngara=?",
        new Object[] {codimp,dittao,ngara});

    //Aggiornamento di W_INVCOMDES #### qui usiamo lo userkey1 anche per RT ####
    sqlManager.update("update W_INVCOMDES set desintest=?,descodsog=? where idanonimo=? and idprg='PG' and idcom in (select idcom from w_invcom where idprg='PG' and coment='GARE' and comkey1=?)",
        new Object[] {ragioneSociale,userkey1,idanonimo,ngara});
    //Aggiornamento di W_INVCOM
    sqlManager.update("update W_INVCOM set committ=? where comkey1=? and idprg='PA' and idcom in (select idcom from w_invcom where idprg='PA' and comkey2=?)",
        new Object[] {ragioneSociale,comkey1,ngara});
    
    //Delete delle occorrenze in w_puser e g1imprconc
    sqlManager.update("delete from W_PUSER where userkey1=? or userkey1 in (select dittao from ditg where rtofferta=?) ", new Object[] {dittao,dittao});
    sqlManager.update("delete from G1IMPRCONC where codimp=? or codimp in (select dittao from ditg where rtofferta=?) ", new Object[] {dittao,dittao});


  }

}
