/*
 * Created on 17/11/16
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import com.lowagie.text.DocumentException;


/**
 * Gestore non standard per la popup dell'invio invito per asta elettronica
 *
 * @author Marcello Caminiti
 */
public class GestoreInvioInvitoAstaElettronica extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestoreInvioInvitoAstaElettronica.class);

  /** Manager per la gestione delle chiavi. */
  private GenChiaviManager genChiaviManager;

  private PgManager pgManager;

  public GestoreInvioInvitoAstaElettronica() {
    super(false);
  }


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per generare le chiavi
    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
  }

  @Override
  public String getEntita() {
    return "TORN";
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

    //variabili per tracciatura eventi
    int livEvento = 3;
    String codEvento = "GA_PUBBLICA_ASTA";
    String oggEvento = "";
    String descrEvento = "Invia invito all'asta elettronica ";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String messageKey = "";
    String insertAERILANCI="Insert into AERILANCI(ID, NGARA, DITTAO, NUMRIL, RIBAUO, IMPOFF) values(?,?,?,?,?,?)";
    String insertAERILPRE="Insert into AERILPRE(ID, IDRIL, NGARA, DITTAO, CONTAF, PREOFF, IMPOFF) values(?,?,?,?,?,?,?)";

    try{
      // lettura dei parametri di input

      String codgar = datiForm.getString("CODGAR");
      String ngara = datiForm.getString("NGARA");
      oggEvento = ngara;

      //Si deve inviare la comunicazione

      ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String numDestinatari = UtilityStruts.getParametroString(this.getRequest(),"numElementiListaDestinatari");
      String numAllegati = UtilityStruts.getParametroString(this.getRequest(),"numElementiListaDoc");
      String integrazioneWSDM = UtilityStruts.getParametroString(this.getRequest(),"integrazioneWSDM");
      String idconfiStringa = UtilityStruts.getParametroString(this.getRequest(),"idconfi");

      try {
        GestorePubblicaSuPortale gestorePubPortale = new GestorePubblicaSuPortale();
        gestorePubPortale.setRequest(this.getRequest());
        String riservatezzaAttiva = ConfigManager.getValore("wsdm.applicaRiservatezza."+idconfiStringa);
        gestorePubPortale.invioInvito(datiForm, profilo, numDestinatari, numAllegati, integrazioneWSDM,riservatezzaAttiva, false, null, null, null, null,idconfiStringa);
      } catch (SQLException e) {
        this.getRequest().setAttribute("invioEseguito", "Errori");
        livEvento = 3;
        errMsgEvento = "Errore nell'invio dell'invito";
        throw new GestoreException("Errore nell'invio dell'invito", null, e);
      } catch (IOException e) {
        this.getRequest().setAttribute("invioEseguito", "Errori");
        livEvento = 3;
        errMsgEvento = "Errore nell'invio dell'invito";
        throw new GestoreException("Errore nell'invio dell'invito", null, e);
      } catch (GestoreException e) {
        this.getRequest().setAttribute("invioEseguito", "Errori");
        throw e;
      }catch (DocumentException e) {
        this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
        livEvento = 3;
        errMsgEvento = "Errore nella creazione del Fascicolo";
        throw new GestoreException("Errore nella creazione del Fascicolo", null, e);
      }

      Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
      try {
        this.sqlManager.update("update GARE set FASGAR = 7, STEPGAR = 65 where NGARA=?",new Object[]{ngara});
        String updateGare1= "update GARE1 set AEDINVIT = ? ";
        if("1".equals(integrazioneWSDM) && !GestioneWSDMManager.getAbilitazioneInvioSingolo(idconfiStringa)){
          String nproti = (String) this.sqlManager.getObject("select nproti from torn where codgar=?", new Object[]{codgar});
          updateGare1 += ", AENPROTI='" + nproti + "'";
        }
        updateGare1 += " where NGARA = ? ";
        this.sqlManager.update(updateGare1,new Object[]{dataOdierna,ngara});
        this.sqlManager.update("update torn set dultagg=? where codgar=?",new Object[]{dataOdierna,codgar});
      } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = "Errore nell'aggiornamento della gara";
        throw new GestoreException("Errore nell'aggiornamento della gara", null, e);
      }

      //Si deve aggiornare lo stato della documentazione di gara, impostando
      // STATODOC.DOCUMGARA=5
      try {
        this.sqlManager.update("update DOCUMGARA set STATODOC = 5, DATARILASCIO = ? where CODGAR=? and NGARA=? and GRUPPO = ? ",new Object[]{dataOdierna,codgar,ngara,new Long(12)});
      } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = "Errore nell'aggiornamento dello stato dei documenti della gara";
        throw new GestoreException("Errore nell'aggiornamento dello stato dei documenti della gara", null, e);
      }

      //Popolamento AERILANCI e AERILPRE
      if(numDestinatari!=null && !"".equals(numDestinatari)){
        String ditta=null;
        Double ribauo = null;
        Double impoff = null;
        int idRil=0;
        int id = 0;
        Long modlicg =  null;

        try {
          modlicg = (Long)this.sqlManager.getObject("select modlicg from gare where ngara=?", new Object[]{ngara});
          List listaDitte = this.sqlManager.getListVector("select dittao, ribauo, impoff from ditg where ngara5=? and codgar5=? and (fasgar > 6 or fasgar = 0 or fasgar is null) and (ammgar='1' or ammgar is null)",  new Object[]{ngara, codgar});
          if(listaDitte!=null && listaDitte.size()>0){
            for(int i= 0; i < listaDitte.size(); i++){
              ditta = SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).getStringValue();
              ribauo = SqlManager.getValueFromVectorParam(listaDitte.get(i), 1).doubleValue();
              impoff = SqlManager.getValueFromVectorParam(listaDitte.get(i), 2).doubleValue();
              idRil = genChiaviManager.getNextId("AERILANCI");
              this.sqlManager.update(insertAERILANCI, new Object[]{new Long(idRil), ngara, ditta, new Long(-1), ribauo, impoff});

              if(modlicg!=null && (modlicg.longValue()==5 ||  modlicg.longValue()==14)){
                List datiDestPrezzi = null;
                datiDestPrezzi= this.sqlManager.getListVector("select contaf, preoff, impoff from dpre where ngara=? and dittao=?", new Object[]{ngara, ditta});
                if(datiDestPrezzi!=null && datiDestPrezzi.size()>0){
                  Long contaff = null;
                  Double preoff = null;
                  for(int j= 0; j < datiDestPrezzi.size(); j++){
                    contaff = SqlManager.getValueFromVectorParam(datiDestPrezzi.get(j), 0).longValue();
                    preoff = SqlManager.getValueFromVectorParam(datiDestPrezzi.get(j), 1).doubleValue();
                    impoff = SqlManager.getValueFromVectorParam(datiDestPrezzi.get(j), 2).doubleValue();
                    id = genChiaviManager.getNextId("AERILPRE");
                    this.sqlManager.update(insertAERILPRE, new Object[]{new Long(id), new Long(idRil), ngara, ditta,contaff, preoff, impoff});
                  }

                }

              }
            }
          }
        } catch (SQLException e) {
          livEvento = 3;
          errMsgEvento = "Errore nella gestione dell'inserimento dei dati in AERILANCI";
          throw new GestoreException("Errore nella gestione 'inserimento dei dati in AERILANCI", null, e);

        }
      }

      livEvento = 1;
      errMsgEvento = "";
      // setta l'operazione a completata, in modo da scatenare il reload della
      // pagina principale
      this.getRequest().setAttribute("invioEseguito", "1");

    } finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }

    }
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
