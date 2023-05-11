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

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.invcom.InvioComunicazioniManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.invcom.DestinatarioComunicazione;
import it.eldasoft.gene.db.domain.invcom.InvioComunicazione;
import it.eldasoft.gene.db.domain.invcom.ModelloComunicazione;
import it.eldasoft.gene.db.domain.invcom.PKInvioComunicazione;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ElencoOperatoriManager;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.sil.pg.tags.funzioni.ControlloVariazioniAggiornamentoDaPortaleFunction;
import it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAcquisisciDaPortale;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.utility.UtilityDate;

public class AcquisizioneElencoManager {

  static Logger logger = Logger.getLogger(AcquisizioneElencoManager.class);

  private SqlManager sqlManager;
  private PgManager pgManager;
  private ElencoOperatoriManager elencoOperatoriManager;
  private FileAllegatoManager fileAllegatoManager;
  private GenChiaviManager genChiaviManager;
  private TabellatiManager tabellatiManager;
  private GeneManager geneManager;
  private InvioComunicazioniManager invioComunicazioniManager;
  private GestioneWSDMManager gestioneWSDMManager;
  private MailManager      mailManager;




  private static final int CODICE_MODELLO_COMUNICAZIONE = 59;

  private final int        NUMERO_TENTATIVI_INVIO = 5;
  private final String     TITOLOMAIL             = "Notifica di iscrizioni a elenco operatori economici o catalogo ";
  private final static String selectAllegati="select idprg,iddocdig from w_docdig where DIGENT = 'W_INVCOM' and digkey1 = ? and idprg = 'PA' and dignomdoc = ? ";


  /**
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * Set PgManager
   *
   * @param pgManager
   */
  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  /**
   * @param elencoOperatoriManager
   */
  public void setElencoOperatoriManager(ElencoOperatoriManager elencoOperatoriManager) {
    this.elencoOperatoriManager = elencoOperatoriManager;
  }



  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }


  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }


  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setInvioComunicazioniManager(InvioComunicazioniManager invioComunicazioniManager) {
    this.invioComunicazioniManager = invioComunicazioniManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  private class MessaggioAcquisizione{
    private String messaggio;
    public static final int iscrizione = 1;
    public static final int aggiornamento = 2;
    public static final int rinnovo = 3;

    MessaggioAcquisizione(String elenco, int modo){
      String msg="Riepilogo acquisizione delle richieste di ";
      if(modo==aggiornamento)
        msg+="aggiornamento";
      else if(modo==rinnovo)
        msg+="rinnovo";
      msg+=" iscrizione per l'elenco/catalogo " + elenco + ":\r\n";
      this.messaggio=msg;
    }


    public String getMessaggio() {
      return messaggio;
    }

    public void setMessaggio(String messaggio) {
      this.messaggio += messaggio;
    }
  }

  /**
   * Gestione dei messaggi FS2 provenienti da portale per iscrizione agli elenchi
   * con ISOPEAUTO=1
   *
   * @throws it.eldasoft.gene.web.struts.tags.gestori.GestoreException
   */
  public void acquisisciIscrizioni() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'integrazione con il
    // Portale Alice (OP114)
    if (WebUtilities.isAppNotReady()) {
      return;
    }
    ServletContext context = SpringAppContext.getServletContext();
    if (!GeneManager.checkOP(context, "OP114")) {
      return;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("acquisisciIscrizioni: inizio metodo");
    }

    Long idcom;
    String ditta;
    String user;
    String ngara;
    String select = "SELECT idcom, userkey1, comkey2, comkey1 FROM w_invcom, "
        + "w_puser WHERE idprg = ? AND comstato = ? AND comtipo = ? AND comkey1 = usernome "
        + "and exists(select * from garealbo where ngara=comkey2 and isopeauto='1') ORDER BY comkey2,comdatastato";
    String idprg = "PA";
    String comstato = "5";
    String comtipo = "FS2";

    List<?> listaIDCOM = null;
    try {
      listaIDCOM = this.sqlManager.getListVector(select, new Object[]{idprg, comstato, comtipo});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
    }
    if (listaIDCOM != null && listaIDCOM.size() > 0) {


      boolean opInserito=false;
      boolean opAbilitato=false;
      boolean mailInviata=false;
      boolean commitStatoErrore=false;

      //variabili per tracciatura eventi
      int livEvento = 1;
      String codEventoIscrizione = "GA_ACQUISIZIONE_ISCRIZIONE";
      String codEventoAbilitazione = "GA_ACQUISIZIONE_ISCRIAUTO";
      String oggEvento = "";
      String descrEventoAbilitazione = "";
      String descrEventoIscrizione = "";
      String descrEventoAbilitazioneIntestazione = "Abilitazione e attivazione automatica operatore in elenco o catalogo";
      String descrEventoIscrizioneIntestazione = "Acquisizione iscrizione a elenco operatori o catalogo da portale Appalti";
      String errMsgEvento = "";
      String descrEventoTemp="";


      //boolean mailInviata=false;
      GestorePopupAcquisisciDaPortale gacqport = new GestorePopupAcquisisciDaPortale();
      gacqport.setFileAllegatoManager(fileAllegatoManager);
      gacqport.setGenChiaviManager(genChiaviManager);
      gacqport.setPgManager(pgManager);
      gacqport.setSqlManager(sqlManager);
      gacqport.setTabellatiManager(tabellatiManager);
      gacqport.setGeneManager(geneManager);


      TransactionStatus status=null;
      boolean doCommit=false;
      HashMap<String, Object> ret;
      int esito=0;

      boolean nuovoElenco=false;
      String msgOpErrore="";
      String msgOp="";


      boolean isMailDestinatari = true;
      IMailSender mailSender = null;

      List<MessaggioAcquisizione> listaMessaggi = new ArrayList<MessaggioAcquisizione>();
      HashMap<String,Integer> hMIndice = new HashMap<String,Integer>();
      MessaggioAcquisizione messaggioElenco=null;

      String destinatari[] = this.getDestinatariEnte();
      if(destinatari==null)
        isMailDestinatari = false;


      int indiceElenco=0;
      int indiceCorrente=0;
      String xml="";
      String codDittaInserita="";

      for (Object listaIDCOM1 : listaIDCOM) {

        status=null;
        commitStatoErrore=false;
        esito=0;
        xml="";
        codDittaInserita="";

        idcom = SqlManager.getValueFromVectorParam(listaIDCOM1, 0).longValue();
        ditta = SqlManager.getValueFromVectorParam(listaIDCOM1, 1).getStringValue();
        ngara = SqlManager.getValueFromVectorParam(listaIDCOM1, 2).getStringValue();
        user = SqlManager.getValueFromVectorParam(listaIDCOM1, 3).getStringValue();

        livEvento = 1;
        oggEvento = ngara;
        errMsgEvento = "";
        descrEventoTemp = " (cod.ditta " + user + ", id.comunicazione " + idcom + ")";
        descrEventoIscrizione = descrEventoIscrizioneIntestazione + descrEventoTemp;

        try {
          status = this.sqlManager.startTransaction();
          ret = elencoOperatoriManager.acquisizioneDaPortaleSingola(idcom, user, ngara, comtipo, true, "", "",gacqport , status, true);
          esito = ((Integer)ret.get("esito")).intValue();
          xml = (String)ret.get("xml");
          if(ElencoOperatoriManager.OK==esito) {
            this.pgManager.aggiornaStatoW_INVOCM(idcom, "6", "");
            doCommit=true;
            opInserito=true;
            codDittaInserita = (String)ret.get("codiceDitta");
          } else {
            doCommit=false;
            opInserito =false;
          }

        } catch (Exception e ) {

          livEvento=3;
          errMsgEvento=e.getMessage();
          doCommit=false;
          opInserito=false;
          commitStatoErrore=true;
          logger.error("acquisisciIscrizione - inserimento operatore " + ditta + " dell'elenco " + ngara +" errore: " + e.getMessage());
        }finally {
          if (status != null) {
            try {
              if (doCommit) {
                this.sqlManager.commitTransaction(status);
              } else {
                this.sqlManager.rollbackTransaction(status);
                if(commitStatoErrore) {
                  this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
                  this.sqlManager.commitTransaction(status);
                }
              }
            } catch (SQLException ex) {
              throw new GestoreException("", "", ex);
            }
          }
          try {
            if(xml != null && !"".equals(xml)) {
              xml = CostantiAppalti.nomeFileXML_Iscrizione + "\r\n" + xml;
              if(errMsgEvento!="")
                errMsgEvento+="\r\n\r\n";
              errMsgEvento+= xml;
            }
            LogEvento logEvento = new LogEvento();
            logEvento.setCodApplicazione("PG");
            logEvento.setOggEvento(oggEvento);
            logEvento.setLivEvento(livEvento);
            logEvento.setCodEvento(codEventoIscrizione);
            logEvento.setDescr(descrEventoIscrizione);
            logEvento.setErrmsg(errMsgEvento);
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
          }
        }

        if(hMIndice.get(ngara)==null) {
          hMIndice.put(ngara, new Integer(indiceCorrente));
          messaggioElenco = new MessaggioAcquisizione(ngara, MessaggioAcquisizione.iscrizione);
          nuovoElenco=true;
          indiceElenco=indiceCorrente;
          indiceCorrente++;
        }else {
          indiceElenco = hMIndice.get(ngara).intValue();
          messaggioElenco = listaMessaggi.get(indiceElenco);
          nuovoElenco=false;
        }
        status=null;
        errMsgEvento="";

        if(opInserito) {
          descrEventoAbilitazione = descrEventoAbilitazioneIntestazione + descrEventoTemp;
          livEvento = 1;

          try {
            status = this.sqlManager.startTransaction();
            Date oggi = new Date(UtilityDate.getDataOdiernaAsDate().getTime());
            Long  numordpl = (Long) this.sqlManager.getObject("select max(numordpl) from ditg where ngara5=?", new Object[] {ngara});
            if (numordpl == null) {
              numordpl = new Long(0);
            }
            numordpl = new Long(numordpl.longValue() + 1);


            String update="update DITG set DABILITAZ=?, ABILITAZ=?, NUMORDPL=?,DATTIVAZ=? where NGARA5=? and DITTAO=?";
            this.sqlManager.update(update, new Object[] {oggi, new Long(1), numordpl, oggi, ngara, codDittaInserita});

            this.pgManager.updatePenalita("$" + ngara,ngara, ditta, status);

            if(nuovoElenco)
              this.sqlManager.update("update gare set fasgar=-4, stepgar=-40 where ngara=?", new Object[] {ngara});


            doCommit=true;
            opAbilitato=true;
          } catch (Exception e ) {
            livEvento=3;
            errMsgEvento=e.getMessage();
            doCommit=false;
            logger.error("acquisisciIscrizione - abilitazione operatore " + ditta + " dell'elenco " + ngara + " errore: " + e.getMessage());
            opAbilitato=false;
          }finally{
            if (status != null) {
              try {
                if (doCommit) {
                  this.sqlManager.commitTransaction(status);
                } else {
                  this.sqlManager.rollbackTransaction(status);
                }
              } catch (SQLException ex) {
                logger.error("Errore nella gestione della transazione per l'abilitazione automatica dell'operatore " + ditta + " dell'elenco " + ngara, ex);
              }
            }
            try {
              LogEvento logEvento = new LogEvento();
              logEvento.setCodApplicazione("PG");
              logEvento.setOggEvento(oggEvento);
              logEvento.setLivEvento(livEvento);
              logEvento.setCodEvento(codEventoAbilitazione);
              logEvento.setDescr(descrEventoAbilitazione);
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }

        }

        if(opAbilitato) {
          try {
            creazioneComunicazione(ngara,ditta);
            mailInviata=true;
          }catch(Exception e) {
            mailInviata=false;
            logger.error("acquisisciIscrizione - invio mail operatore " + ditta + " dell'elenco " + ngara + " errore: " + e.getMessage());
          }
        }


        //Creazione del messaggio per l'invio all'ente
        if(isMailDestinatari ) {
          msgOp="";
          msgOpErrore="";

          String nomimo = null;
          try {
            nomimo = (String)this.sqlManager.getObject("select nomimo from ditg where ngara5=? and dittao=?", new Object[] {ngara, ditta});
          } catch (SQLException e) {
            logger.error("acquisisciIscrizione - invio mail all'ente errore: " + e.getMessage());
          }
          if(opInserito) {
            msgOp+="Operatore: " + ditta;
            if(nomimo != null)
              msgOp += " - " + nomimo;
            if(opAbilitato || mailInviata) {
              msgOp+= " (";
              if(opAbilitato)
                msgOp+="abilitato e attivato";
              if(mailInviata) {
                if(opAbilitato)
                  msgOp+= ", ";
                msgOp+="con invio della mail di abilitazione";
              }
              msgOp+= ")";
              msgOp+="\r\n";
            }
          }else {
            msgOpErrore+="Operatore: " + ditta;
            if(nomimo != null)
              msgOpErrore += " - " + nomimo;
            msgOpErrore+=" non acquisito per errori\r\n";
          }

          if(msgOp!="")
            messaggioElenco.setMessaggio(msgOp);
          if(msgOpErrore!="")
            messaggioElenco.setMessaggio(msgOpErrore);

          if(nuovoElenco)
            listaMessaggi.add(indiceElenco,messaggioElenco);
          else
            listaMessaggi.set(indiceElenco, messaggioElenco);
        }

      }


      if(isMailDestinatari && listaMessaggi!=null && listaMessaggi.size()>0) {
        String messaggio="";
        for (MessaggioAcquisizione messaggioEl : listaMessaggi) {
          messaggio+=messaggioEl.getMessaggio() + "\r\n";
        }
        int numeroTentativi = this.NUMERO_TENTATIVI_INVIO;
        mailInviata = true;

        try {
          mailSender = MailUtils.getInstance(this.mailManager,"PG",CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
        } catch (Exception e) {
          logger.error("acquisisciIscrizione - invio mail all'ente errore: " + e.getMessage());
        }
        // Tentativi di invio
        if(mailSender!=null) {
          do {
            try {
              mailSender.send(destinatari, null, null, TITOLOMAIL, messaggio, null);
              mailInviata = true;
            } catch (MailSenderException ms) {
              numeroTentativi--;
              mailInviata = false;
              int tentativo = this.NUMERO_TENTATIVI_INVIO - numeroTentativi;
              logger.error("Errore durante l'invio della mail, tentativo numero " + tentativo + ": " + ms.getMessage(), ms);
            }
          } while (numeroTentativi > 0 && !mailInviata);
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("acquisisciIscrizioni: fine metodo");
    }
  }


  /**
   * Gestione dei messaggi FS4 provenienti da portale per iscrizione agli elenchi
   * con ISOPEAUTO=1
   *
   * @throws it.eldasoft.gene.web.struts.tags.gestori.GestoreException
   */
  public void acquisisciAggiornamentoIscrizioni() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'integrazione con il
    // Portale Alice (OP114)
    if (WebUtilities.isAppNotReady()) {
      return;
    }
    ServletContext context = SpringAppContext.getServletContext();
    if (!GeneManager.checkOP(context, "OP114")) {
      return;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("acquisisciAggiornamentoIscrizioni: inizio metodo");
    }

    Long idcom;
    String ditta;
    String user;
    String ngara;
    String select = "SELECT idcom, userkey1, comkey2, comkey1 FROM w_invcom, "
        + "w_puser WHERE idprg = ? AND comstato = ? AND comtipo = ? AND comkey1 = usernome "
        + "and exists(select * from garealbo where ngara=comkey2 and isopeauto='1') ORDER BY comkey2,comdatastato";
    String idprg = "PA";
    String comstato = "5";
    String comtipo = "FS4";

    List<?> listaIDCOM = null;
    try {
      listaIDCOM = this.sqlManager.getListVector(select, new Object[]{idprg, comstato, comtipo});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
    }
    if (listaIDCOM != null && listaIDCOM.size() > 0) {
      boolean fs2Pendenti=false;
      boolean garacquisizPendenti = false;
      boolean dittaNonPresente = false;
      boolean dittaPiuRt =false;
      boolean opAggiornato=false;
      boolean commitStatoErrore=false;

      GestorePopupAcquisisciDaPortale gacqport = new GestorePopupAcquisisciDaPortale();
      gacqport.setFileAllegatoManager(fileAllegatoManager);
      gacqport.setGenChiaviManager(genChiaviManager);
      gacqport.setPgManager(pgManager);
      gacqport.setSqlManager(sqlManager);
      gacqport.setTabellatiManager(tabellatiManager);
      gacqport.setGeneManager(geneManager);

      TransactionStatus status=null;
      boolean doCommit=false;
      HashMap<String, Object> ret;
      int esito=0;

      boolean isMailDestinatari = true;
      boolean nuovoElenco=false;
      IMailSender mailSender = null;
      String msgOpErrore="";
      String msgOp="";

      List<MessaggioAcquisizione> listaMessaggi = new ArrayList<MessaggioAcquisizione>();
      HashMap<String,Integer> hMIndice = new HashMap<String,Integer>();
      MessaggioAcquisizione messaggioElenco;

      ControlloVariazioniAggiornamentoDaPortaleFunction functionControlloVariazioni = null;

      String destinatari[] = this.getDestinatariEnte();
      if(destinatari==null)
        isMailDestinatari = false;

      int indiceElenco=0;
      int indiceCorrente=0;
      Long numFs2Pendenti=null;
      Long numGaracquisizPendenti=null;

      BlobFile fileAllegato = null;
      Vector<?> datiW_DOCDIG = null;
      Long iddocdig=null;
      String idprgW_INVCOM=null;
      String xml=null;
      AggiornamentoIscrizioneImpresaElencoOperatoriDocument document = null;
      String messaggioCategorie="";
      String messaggioDoc="";

      //variabili per tracciatura eventi
      int livEvento = 1;
      String codEvento = "GA_ACQUISIZIONE_INTEGRAZIONE";
      String oggEvento = "";
      String descrEvento = "";
      String descrEventoIntestazione = "Acquisizione integrazione dati/documenti a iscrizione in elenco operatori o catalogo da portale Appalti";
      String errMsgEvento = "";
      String descrEventoTemp="";

      for (Object listaIDCOM1 : listaIDCOM) {
        commitStatoErrore=false;
        esito=0;
        idcom = SqlManager.getValueFromVectorParam(listaIDCOM1, 0).longValue();
        ditta = SqlManager.getValueFromVectorParam(listaIDCOM1, 1).getStringValue();
        ngara = SqlManager.getValueFromVectorParam(listaIDCOM1, 2).getStringValue();
        user = SqlManager.getValueFromVectorParam(listaIDCOM1, 3).getStringValue();

        dittaNonPresente=false;
        dittaPiuRt =false;
        doCommit=false;
        opAggiornato =false;
        livEvento = 1;
        errMsgEvento="";
        xml="";

        oggEvento=ngara;
        descrEventoTemp = " (cod.ditta " + ditta + ", id.comunicazione " + idcom + ")";
        descrEvento = descrEventoIntestazione + descrEventoTemp;
        try {
          numFs2Pendenti = (Long)this.sqlManager.getObject("select count(*) from w_invcom where idprg='PA' and comkey2=? and comtipo='FS2' and comstato=5 and comkey1=?", new Object[] {ngara, user});
          if(numFs2Pendenti!=null && numFs2Pendenti.longValue()>0) {
            fs2Pendenti=true;
            errMsgEvento="Deve essere prima acquisita la richiesta di iscrizione (FS2)";
            logger.error("acquisisciAggiornamentoIscrizioni - aggiornamento operatore " + ditta + " dell'elenco "+ ngara + " errore: deve essere prima acquisita la richiesta di iscrizione (FS2)");
          }else
            fs2Pendenti=false;

          numGaracquisizPendenti = (Long)this.sqlManager.getObject("select count(*) from garacquisiz where ngara=? and stato=1 and codimp=?", new Object[] {ngara,ditta});
          if(numGaracquisizPendenti!=null && numGaracquisizPendenti.longValue()>0) {
            garacquisizPendenti=true;
            errMsgEvento+="Ci sono richieste di modifica alle categorie d'iscrizione pendenti, da acquisire da interfaccia (garacquisiz)";
            logger.error("acquisisciAggiornamentoIscrizioni - aggiornamento operatore " + ditta + " dell'elenco "+ ngara + " errore: Ci sono richieste di modifica alle categorie d'iscrizione pendenti da acquisire da interfaccia (garacquisiz)");
          }else
            garacquisizPendenti=false;

          if(!fs2Pendenti && !garacquisizPendenti) {

            String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(ditta, ngara, "$" + ngara,null);
            if("0".equals(datiControllo[0])) {
              dittaNonPresente=true;
              livEvento=3;
              errMsgEvento="La ditta non risulta iscritta in elenco";
              logger.error("acquisisciAggiornamentoIscrizioni - aggiornamento operatore " + ditta + " dell'elenco "+ ngara + " errore: la ditta non risulta iscritta in elenco");
            }else if("2".equals(datiControllo[0])) {
              dittaPiuRt =true;
              livEvento=3;
              errMsgEvento="La ditta risulta mandataria di più RT iscritti in elenco";
              logger.error("acquisisciAggiornamentoIscrizioni - aggiornamento operatore " + ditta + " dell'elenco "+ ngara + " errore: la ditta risulta mandataria di più RT iscritti in elenco");
            }else
              ditta = datiControllo[1];

            if(!dittaNonPresente && !dittaPiuRt) {
              datiW_DOCDIG = sqlManager.getVector(selectAllegati, new Object[]{idcom.toString(), CostantiAppalti.nomeFileXML_Aggiornamento});

              if(datiW_DOCDIG != null ){
                if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
                  idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

                if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
                  iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
              }

              fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
              if(fileAllegato!=null && fileAllegato.getStream()!=null){
                xml = new String(fileAllegato.getStream());
              }

              document = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
              functionControlloVariazioni = new ControlloVariazioniAggiornamentoDaPortaleFunction();
              messaggioCategorie = functionControlloVariazioni.controlloCategorie(document, ngara, ditta, sqlManager, tabellatiManager, pgManager);
              messaggioDoc = functionControlloVariazioni.controlloDocumenti(document);

              status = this.sqlManager.startTransaction();
              ret = elencoOperatoriManager.acquisizioneDaPortaleSingola(idcom, user, ngara, comtipo, true, "", messaggioCategorie + "\r\n" + messaggioDoc,gacqport , status);
              esito = ((Integer)ret.get("esito")).intValue();
              if(ElencoOperatoriManager.OK==esito) {
                this.pgManager.aggiornaStatoW_INVOCM(idcom, "6", "");
                doCommit=true;
                opAggiornato=true;
              }
            }
          }else {
            livEvento=3;
          }
        } catch (Exception e ) {
          commitStatoErrore=true;
          doCommit=false;
          opAggiornato=false;
          livEvento=3;
          errMsgEvento=e.getMessage();
          logger.error("acquisisciAggiornamentoIscrizioni - aggiornamento operatore errore: " + e.getMessage());
        }finally {
          if (status != null) {
            try {
              if (doCommit) {
                this.sqlManager.commitTransaction(status);
              } else {
                this.sqlManager.rollbackTransaction(status);
                if(commitStatoErrore) {
                  this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
                  this.sqlManager.commitTransaction(status);
                }
              }
            } catch (SQLException ex) {
              throw new GestoreException("", "", ex);
            }
          }

          try {
            if(xml != null && !"".equals(xml)) {
              xml = CostantiAppalti.nomeFileXML_Aggiornamento + "\r\n" + xml;
              if(errMsgEvento!="")
                errMsgEvento+="\r\n\r\n";
              errMsgEvento+= xml;
            }
            LogEvento logEvento = new LogEvento();
            logEvento.setCodApplicazione("PG");
            logEvento.setOggEvento(oggEvento);
            logEvento.setLivEvento(livEvento);
            logEvento.setCodEvento(codEvento);
            logEvento.setDescr(descrEvento);
            logEvento.setErrmsg(errMsgEvento);
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
          }

        }


        //Creazione del messaggio per l'invio all'ente
        if(isMailDestinatari ) {
          msgOp="";
          msgOpErrore="";

          if(hMIndice.get(ngara)==null) {
            hMIndice.put(ngara, new Integer(indiceCorrente));
            messaggioElenco = new MessaggioAcquisizione(ngara,MessaggioAcquisizione.aggiornamento);
            nuovoElenco=true;
            indiceElenco=indiceCorrente;
            indiceCorrente++;
          }else {
            indiceElenco = hMIndice.get(ngara).intValue();
            messaggioElenco = listaMessaggi.get(indiceElenco);
            nuovoElenco=false;
          }

          String nomimo = null;
          try {
            nomimo = (String)this.sqlManager.getObject("select nomimo from ditg where ngara5=? and dittao=?", new Object[] {ngara, ditta});
          } catch (SQLException e) {
            logger.error("acquisisciAggiornamentoIscrizioni - invio mail all'ente errore: " + e.getMessage());
          }
          if(opAggiornato) {
            msgOp+="Operatore: " + ditta;
            if(nomimo != null)
              msgOp += " - " + nomimo;
            msgOp+=", aggiornato\r\n";
          }else {
            msgOpErrore+="Operatore: " + ditta;
            if(nomimo != null)
              msgOpErrore += " - " + nomimo;
            msgOpErrore+= ", non aggiornato";
            if(fs2Pendenti || garacquisizPendenti) {
              msgOpErrore+= " perchè ";
              if(fs2Pendenti) {
                msgOpErrore+= "deve essere prima acquisita la richiesta di iscrizione";
                if(garacquisizPendenti)
                  msgOpErrore+= " e ";
              }
              if(garacquisizPendenti)
                msgOpErrore+= "ci sono richieste di modifica alle categorie d'iscrizione pendenti (completare l'acquisizione da interfaccia)";
            }else
              msgOpErrore+= " per errori";
            msgOpErrore+= "\r\n";
          }

          if(msgOp!="")
            messaggioElenco.setMessaggio(msgOp);
          if(msgOpErrore!="")
            messaggioElenco.setMessaggio(msgOpErrore);

          if(nuovoElenco)
            listaMessaggi.add(indiceElenco,messaggioElenco);
          else
            listaMessaggi.set(indiceElenco, messaggioElenco);
        }

      }

      boolean mailInviata=false;
      if(isMailDestinatari && listaMessaggi!=null && listaMessaggi.size()>0) {
        String messaggio="";
        for (MessaggioAcquisizione messaggioEl : listaMessaggi) {
          messaggio+=messaggioEl.getMessaggio() + "\r\n";
        }
        int numeroTentativi = this.NUMERO_TENTATIVI_INVIO;
        mailInviata = true;

        try {
          mailSender = MailUtils.getInstance(this.mailManager,"PG",CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
        } catch (Exception e) {
          logger.error("acquisisciAggiornamentoIscrizioni - invio mail all'ente errore: " + e.getMessage());
        }
        // Tentativi di invio
        if(mailSender!=null) {
          do {
            try {
              mailSender.send(destinatari, null, null, TITOLOMAIL, messaggio, null);
              mailInviata = true;
            } catch (MailSenderException ms) {
              numeroTentativi--;
              mailInviata = false;
              int tentativo = this.NUMERO_TENTATIVI_INVIO - numeroTentativi;
              logger.error("Errore durante l'invio della mail, tentativo numero " + tentativo + ": " + ms.getMessage(), ms);
            }
          } while (numeroTentativi > 0 && !mailInviata);
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("acquisisciAggiornamentoIscrizioni: fine metodo");
    }
  }

  private void creazioneComunicazione(String ngara, String ditta) throws Exception {
    ModelloComunicazione modello = this.invioComunicazioniManager.getModelloComunicazioneByGenere(CODICE_MODELLO_COMUNICAZIONE);
    InvioComunicazione comunicazione = this.invioComunicazioniManager.createComunicazioneFromModello(modello);
    comunicazione.getPk().setIdProgramma("PG");

    String oggetto="";
    String codrup= "";
    String nomtecrup="";
    String cftecrup="";
    Long inctec = null;
    String intestazione=null;
    String pec=null;
    String email=null;
    String indirizzo=null;
    Integer tipoIndirizzo=null;
    String select="select OGGETTO, codrup from garealbo g, torn t where ngara=? and g.codgar=t.codgar";

    Vector<?> dati = this.sqlManager.getVector(select, new Object[] {ngara});
    if(dati!=null && dati.size()>0) {
      oggetto=SqlManager.getValueFromVectorParam(dati, 0).stringValue();
      codrup = SqlManager.getValueFromVectorParam(dati, 1).stringValue();
      if(codrup!=null && !"".equals(codrup)) {
        select="select nomtec, cftec,inctec from tecni where codtec=?";
        dati = this.sqlManager.getVector(select, new Object[] {codrup});
        if(dati!=null && dati.size()>0) {
          nomtecrup=SqlManager.getValueFromVectorParam(dati, 0).stringValue();
          cftecrup=SqlManager.getValueFromVectorParam(dati, 1).stringValue();
          inctec=SqlManager.getValueFromVectorParam(dati, 2).longValue();
        }
      }
      select="select nomimp, emai2ip,emaiip from impr where codimp=?";
      dati = this.sqlManager.getVector(select, new Object[] {ditta});
      if(dati!=null && dati.size()>0) {
        intestazione=SqlManager.getValueFromVectorParam(dati, 0).stringValue();
        pec=SqlManager.getValueFromVectorParam(dati, 1).stringValue();
        email=SqlManager.getValueFromVectorParam(dati, 2).stringValue();
        if(pec!=null && !"".equals(pec)) {
          indirizzo=pec;
          tipoIndirizzo=new Integer(1);
        }else {
          indirizzo=email;
          tipoIndirizzo=new Integer(2);
        }
      }
    }


    comunicazione.setEntita("GARE");
    comunicazione.setChiave1(ngara);

    comunicazione.setNomeMittente(ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO));
    String testo = comunicazione.getTesto();
    HashMap<String, Object> parametri = new HashMap<String, Object>();
    parametri.put("oggetto", testo);
    parametri.put("ngara", ngara);
    parametri.put("codgar", "$"+ngara);
    parametri.put("oggettoga", oggetto);
    parametri.put("nomtecrup", nomtecrup);
    parametri.put("cftecrup", cftecrup);
    String incRup = null;
    if(inctec!=null)
      incRup =inctec.toString();
    parametri.put("inctec", incRup);

    testo = pgManager.sostituzioneMnemonici(parametri);
    comunicazione.setTesto(testo);

    String oggettoCom = comunicazione.getOggetto();
    parametri.replace("oggetto", oggettoCom);
    oggettoCom = pgManager.sostituzioneMnemonici(parametri);
    comunicazione.setOggetto(oggettoCom);

    comunicazione.setComunicazionePubblica(InvioComunicazione.COMUNICAZIONE_RISERVATA);
    comunicazione.setDataPubblicazione(new java.util.Date());
    comunicazione.setStato(InvioComunicazione.STATO_COMUNICAZIONE_BOZZA);

    //Destinatario
    DestinatarioComunicazione destinatario = new DestinatarioComunicazione();
    destinatario.setEntitaArchivioDestinatario("IMPR");
    destinatario.setCodiceSoggettoArchivio(ditta);
    destinatario.setIndirizzo(indirizzo);
    destinatario.setTipoIndirizzo(tipoIndirizzo);
    destinatario.setIntestazione(intestazione);

    comunicazione.getDestinatariComunicazione().add(destinatario);

    this.invioComunicazioniManager.insertComunicazione(comunicazione);

    PKInvioComunicazione pk = comunicazione.getPk();
    Long idcom = pk.getIdComunicazione();

    gestioneWSDMManager.aggiungiAllegatoSintesiConTransazione(ngara, "", oggettoCom, testo, "PG", idcom, "GARE", null);
    this.invioComunicazioniManager.updateStatoComunicazione(pk, InvioComunicazione.STATO_COMUNICAZIONE_IN_USCITA);
  }

  private String[] getDestinatariEnte() {
    List listaDestinatari = tabellatiManager.getTabellato("A1082");
    if(listaDestinatari==null || (listaDestinatari!=null && listaDestinatari.size()==0)){
      logger.info("Mail di controllo iscrizioni/aggiornamneto elenchi e cataloghi NON INVIABILE, in quanto non sono stati configurati gli indirizzi mail dei destinatari (parametro A1082).");
      return null;
    }

    String destinatari[] = new String[listaDestinatari.size()];
    for(int z=0;z<listaDestinatari.size();z++){
      destinatari[z]= ((Tabellato) listaDestinatari.get(z)).getDescTabellato();
      if(destinatari[z]== null || "".equals(destinatari[z])) {
        logger.error("Mail di controllo iscrizioni/aggiornamento elenchi e cataloghi NON INVIABILE in quanto nel parametro A1082 vi sono delle righe non valorizzate correttamente.");
        return null;
      }
    }

    return destinatari;
  }

  /**
   * Gestione dei messaggi FS3 provenienti da portale per il rinnovo delle
   * iscrizione agli elenchi
   *
   * @throws it.eldasoft.gene.web.struts.tags.gestori.GestoreException
   */
  public void acquisisciRinnoviIscrizione() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'integrazione con il
    // Portale Alice (OP114)
    if (WebUtilities.isAppNotReady()) {
      return;
    }
    ServletContext context = SpringAppContext.getServletContext();
    if (!GeneManager.checkOP(context, "OP114")) {
      return;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("acquisisciRinnoviIscrizione: inizio metodo");
    }

    Long idcom;
    String user;
    String ngara;
    String dataIns;
    String datastato=null;
    String comkey1;

    String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "comdatastato" });

    String select = "SELECT idcom, userkey1, comkey2, comdatins, " + dbFunctionDateToString + ",comkey1 FROM w_invcom, "
        + "w_puser WHERE idprg = ? AND comstato = ? AND comtipo = ? AND comkey1 = usernome ORDER BY comkey2, comdatastato, idcom";
    String idprg = "PA";
    String comstato = "5";
    String comtipo = "FS3";
    boolean operatoreTrovato=true;

    List<?> listaIDCOM = null;
    try {
      listaIDCOM = this.sqlManager.getListVector(select, new Object[]{idprg, comstato, comtipo});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
    }
    if (listaIDCOM != null && listaIDCOM.size() > 0) {

      //variabili per tracciatura eventi
      int livEvento = 1;
      String codEvento = "GA_ACQUISIZIONE_RINNOVO";
      String oggEvento = "";
      String descrEventoTmp = "Acquisizione rinnovo iscrizione a elenco operatori/catalogo da portale Appalti";
      String descrEvento="";
      String errMsgEvento = "";

      Long numFs2Pendenti=null;
      boolean fs2Pendenti=false;
      boolean isMailDestinatari=true;

      String destinatari[] = this.getDestinatariEnte();
      if(destinatari==null)
        isMailDestinatari = false;

      List<MessaggioAcquisizione> listaMessaggi = new ArrayList<MessaggioAcquisizione>();
      HashMap<String,Integer> hMIndice = new HashMap<String,Integer>();
      MessaggioAcquisizione messaggioElenco=null;
      String xml=null;

      int indiceElenco=0;
      int indiceCorrente=0;
      boolean nuovoElenco=false;
      boolean rinnovoAcquisito=false;

      String msgOk="";
      String msgNok="";

      for (Object listaIDCOM1 : listaIDCOM) {
        idcom = SqlManager.getValueFromVectorParam(listaIDCOM1, 0).longValue();
        user = SqlManager.getValueFromVectorParam(listaIDCOM1, 1).getStringValue();
        ngara = SqlManager.getValueFromVectorParam(listaIDCOM1, 2).getStringValue();
        dataIns = SqlManager.getValueFromVectorParam(listaIDCOM1, 3).getStringValue();
        datastato = SqlManager.getValueFromVectorParam(listaIDCOM1, 4).getStringValue();
        comkey1 = SqlManager.getValueFromVectorParam(listaIDCOM1, 5).getStringValue();

        livEvento = 1;
        oggEvento = ngara;
        errMsgEvento = "";

        rinnovoAcquisito=false;
        xml="";

        Long genere = null;
        try {
          genere = (Long)this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{ngara});
        } catch (SQLException e) {
          livEvento = 3;
          errMsgEvento=e.getMessage();
        }
        if(genere!=null){
          if(genere.longValue()==10)
            descrEventoTmp = "Acquisizione rinnovo iscrizione a elenco operatori da portale Appalti";
          else if(genere.longValue()==20)
            descrEventoTmp = "Acquisizione rinnovo iscrizione a catalogo da portale Appalti";
        }
        descrEvento = descrEventoTmp + " (cod.ditta " + user + ", id.comunicazione " + idcom + ")";

        try {
          numFs2Pendenti = (Long)this.sqlManager.getObject("select count(*) from w_invcom where idprg='PA' and comkey2=? and comtipo='FS2' and comstato=5 and comkey1=?", new Object[] {ngara,comkey1});
          if(numFs2Pendenti!=null && numFs2Pendenti.longValue()>0) {
            fs2Pendenti=true;
            errMsgEvento = "acquisisciAggiornamentoIscrizioni - aggiornamento operatore " + user + " dell'elenco "+ ngara + " errore: ci sono FS2 da acquisire";
            logger.error(errMsgEvento);
            livEvento = 3;
          }else
            fs2Pendenti=false;
        } catch (SQLException e) {
          fs2Pendenti=true;
          livEvento = 3;
          errMsgEvento=e.getMessage();
        }

        if(!fs2Pendenti) {
          //Si deve controllare che l'impresa sia presente in gara sia direttamente che come
          //mandataria di una RT
          try {
            String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(user, ngara, "$" + ngara, null);
            if("0".equals(datiControllo[0]) || "2".equals(datiControllo[0])){
              this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
              String msg="La ditta " + user + " che ha fatto richiesta di rinnovo d'iscrizione";
              if("0".equals(datiControllo[0]))
                msg+=" non è presente fra gli operatori dell'elenco " + ngara;
              else if("2".equals(datiControllo[0]))
                msg+=" è presente come mandataria in più raggruppamenti temporanei dell'elenco " + ngara;
              logger.error(msg);
              operatoreTrovato=false;
              livEvento=3;
              errMsgEvento=msg;
            }else{
              user = datiControllo[1];
              operatoreTrovato=true;
            }
          } catch (GestoreException e) {
            operatoreTrovato=false;
            this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
            logger.error("Errore nella ricerca del richiedente della richiesta di rinnovo dell'elenco " + ngara, e);
            livEvento=3;
            errMsgEvento=e.getMessage();
          }


          try {
            if(operatoreTrovato) {
              xml = this.elencoOperatoriManager.rinnovoIscrizione(idcom, user, ngara, dataIns, genere,datastato);
              rinnovoAcquisito=true;
              if(xml==null)
                xml="";
            }
          } catch (Exception e) {
            this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
            livEvento=3;
            errMsgEvento=e.getMessage();
          }
        }else {
          this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
        }

        try {
          if(xml != null && !"".equals(xml)) {
            xml = CostantiAppalti.NOME_FILE_RINNOVO_ISCRIZIONE + "\r\n" + xml;
            if(errMsgEvento!="")
              errMsgEvento+="\r\n\r\n";
            errMsgEvento+= xml;
          }
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setOggEvento(oggEvento);
          logEvento.setLivEvento(livEvento);
          logEvento.setCodEvento(codEvento);
          logEvento.setDescr(descrEvento);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }


        //Creazione del messaggio per l'invio all'ente
        if(isMailDestinatari ) {
          msgOk="";
          msgNok="";

          if(hMIndice.get(ngara)==null) {
            hMIndice.put(ngara, new Integer(indiceCorrente));
            messaggioElenco = new MessaggioAcquisizione(ngara,MessaggioAcquisizione.rinnovo);
            nuovoElenco=true;
            indiceElenco=indiceCorrente;
            indiceCorrente++;
          }else {
            indiceElenco = hMIndice.get(ngara).intValue();
            messaggioElenco = listaMessaggi.get(indiceElenco);
            nuovoElenco=false;
          }

          String nomimo = null;
          try {
            nomimo = (String)this.sqlManager.getObject("select nomimo from ditg where ngara5=? and dittao=?", new Object[] {ngara, user});
          } catch (SQLException e) {
            logger.error("acquisisciAggiornamentoIscrizioni - invio mail all'ente errore: " + e.getMessage());
          }
          if(rinnovoAcquisito) {
            msgOk+="Operatore: " + user;
            if(nomimo != null)
              msgOk += " - " + nomimo;
            msgOk+=", rinnovo iscrizione eseguito\r\n";
          }else {
            msgNok+="Operatore: " + user;
            if(nomimo != null)
              msgNok += " - " + nomimo;
            msgNok+= ", rinnovo iscrizione non eseguito";
            if(fs2Pendenti) {
              msgNok+= " perchè vi sono precedenti richieste di iscrizione pendenti";
            }else
              msgNok+= " per errori";
            msgNok+= "\r\n";
          }

          if(msgOk!="")
            messaggioElenco.setMessaggio(msgOk);
          if(msgNok!="")
            messaggioElenco.setMessaggio(msgNok);

          if(nuovoElenco)
            listaMessaggi.add(indiceElenco,messaggioElenco);
          else
            listaMessaggi.set(indiceElenco, messaggioElenco);
        }
      }

      boolean mailInviata=false;
      if(isMailDestinatari && listaMessaggi!=null) {
        String messaggio="";
        for (MessaggioAcquisizione messaggioEl : listaMessaggi) {
          messaggio+=messaggioEl.getMessaggio() + "\r\n";
        }
        int numeroTentativi = this.NUMERO_TENTATIVI_INVIO;
        mailInviata = true;

        IMailSender mailSender=null;
        try {
          mailSender = MailUtils.getInstance(this.mailManager,"PG",CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
        } catch (Exception e) {
          logger.error("acquisisciAggiornamentoIscrizioni - invio mail all'ente errore: " + e.getMessage());
        }
        // Tentativi di invio
        if(mailSender!=null) {
          do {
            try {
              mailSender.send(destinatari, null, null, TITOLOMAIL, messaggio, null);
              mailInviata = true;
            } catch (MailSenderException ms) {
              numeroTentativi--;
              mailInviata = false;
              int tentativo = this.NUMERO_TENTATIVI_INVIO - numeroTentativi;
              logger.error("Errore durante l'invio della mail, tentativo numero " + tentativo + ": " + ms.getMessage(), ms);
            }
          } while (numeroTentativi > 0 && !mailInviata);
        }
      }

    }
    if (logger.isDebugEnabled()) {
      logger.debug("acquisisciRinnoviIscrizione: fine metodo");
    }
  }

}
