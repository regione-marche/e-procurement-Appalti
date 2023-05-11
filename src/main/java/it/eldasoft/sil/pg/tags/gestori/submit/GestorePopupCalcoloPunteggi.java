/*
 * Created on 11/07/17
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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.CalcoloPunteggiManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.pg.ws.rest.cg.ResponseStatus;
import it.eldasoft.utils.spring.UtilitySpring;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire il calcolo
 * dei punteggi lanciato dal popCalcoloPunteggi.jp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupCalcoloPunteggi extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupCalcoloPunteggi.class);

  public GestorePopupCalcoloPunteggi() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private PgManager  pgManager;

  private CalcoloPunteggiManager  calcoloPunteggiManager;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    calcoloPunteggiManager = (CalcoloPunteggiManager) UtilitySpring.getBean("calcoloPunteggiManager",
        this.getServletContext(), CalcoloPunteggiManager.class);
  }

  @Override
  public String getEntita() {
    return "DPUN";
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
    String codEvento = "GA_OEPV_PUNTEGGI_TEC";
    String oggEvento = "";
    String descrEvento = "Calcolo punteggi ditte per criteri di valutazione busta tecnica";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");

    String codice = UtilityStruts.getParametroString(this.getRequest(),"codice");
    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String tipo= UtilityStruts.getParametroString(this.getRequest(),"tipo");
    String bustalotti=UtilityStruts.getParametroString(this.getRequest(),"bustalotti");

    oggEvento = ngara;
    if(CalcoloPunteggiManager.CALCOLO_PUNT_ECO.equals(tipo)){
      codEvento = "GA_OEPV_PUNTEGGI_ECO";
      descrEvento = "Calcolo punteggi ditte per criteri di valutazione busta economica";
    }

    try{

      ResponseStatus esito = calcoloPunteggiManager.calcolaPunteggi(codice, ngara, tipo, CalcoloPunteggiManager.CALCOLO_DA_APPALTI).getStatoCalcolo();
      switch(esito) {
        case ERRORE_LETTURA_DITG:
          livEvento = 3;
          this.getRequest().setAttribute("calcoloEseguito", "2");
          errMsgEvento="Errore nella lettura delle ditte in gara";
          throw new GestoreException(errMsgEvento, "calcoloPunteggi");
        case ERRORE_LETTURA_G1CRIDEF:
          livEvento = 3;
          this.getRequest().setAttribute("calcoloEseguito", "2");
          errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi.G1CRIDEF");
          throw new GestoreException(errMsgEvento, "calcoloPunteggi.G1CRIDEF");
        case ERRORE_LETTURA_IMPAPP:
          livEvento = 3;
          this.getRequest().setAttribute("calcoloEseguito", "2");
          errMsgEvento="Errore nella lettura dell'importo della gara " + ngara;
          throw new GestoreException(errMsgEvento, "calcoloPunteggi");
        case NO_DITTE:
          livEvento = 1;
          errMsgEvento = "";
          this.getRequest().setAttribute("calcoloEseguito", "1");
          break;
        case OK:
          livEvento = 1;
          errMsgEvento = "";
          this.getRequest().setAttribute("calcoloEseguito", "1");
          int numeroStepAttivo = GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA;
          if(CalcoloPunteggiManager.CALCOLO_PUNT_ECO.equals(tipo))
            numeroStepAttivo = GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE;
          pgManager.aggiornaFaseGara(new Long(numeroStepAttivo), ngara, false);
          if("2".equals(bustalotti) || "1".equals(bustalotti)){
            String codiceTornata = (String)this.sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
            Long valoreFasgarAggiornamento = pgManager.getStepAttivo(new Long(numeroStepAttivo), codiceTornata);
            // Aggiornamento del FASGAR dell'occorrenza complementare
            pgManager.aggiornaFaseGara(valoreFasgarAggiornamento, codiceTornata, false);
          }
          break;
      }

    } catch (SQLException e) {
      livEvento = 3;
      this.getRequest().setAttribute("calcoloEseguito", "2");
      errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi");
      throw new GestoreException(errMsgEvento, "calcoloPunteggi",e);
    }catch (GestoreException e) {
      livEvento = 3;
      this.getRequest().setAttribute("calcoloEseguito", "2");
      errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi");
      throw e;
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
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
        this.getRequest().setAttribute("calcoloEseguito", "2");
      }
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }
}
