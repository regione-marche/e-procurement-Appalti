/*
 * Created on 13/07/17
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ElencoOperatoriManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire l'annullamento
 * del calcolo dei punteggi lanciato da popupAnnullaCalcoloPunteggi.jp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupConteggioImportoAggiudicato extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupConteggioImportoAggiudicato.class);

  public GestorePopupConteggioImportoAggiudicato() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private ElencoOperatoriManager elencoOperatoriManager = null;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    elencoOperatoriManager = (ElencoOperatoriManager) UtilitySpring.getBean("elencoOperatoriManager",
        this.getServletContext(), ElencoOperatoriManager.class);
  }

  @Override
  public String getEntita() {
    return "DITG";
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
    String codEvento = "GA_IMPORTOAGG_ELENCO";
    String oggEvento = "";
    String descrEvento = "Calcolo importo aggiudicato nel periodo dell'operatore in elenco/catalogo";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");

    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    oggEvento = ngara;


    try{
      try {
        this.elencoOperatoriManager.conteggioImportoAggiudicatoNelPeriodo(ngara);
      } catch (Exception e) {
        livEvento = 3;
        this.getRequest().setAttribute("calcoloEseguito", "2");
        errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloImportoAggiudicatoPeriodo");
        throw new GestoreException(errMsgEvento, "calcoloImportoAggiudicatoPeriodo",e);
      }

      livEvento = 1;
      errMsgEvento = "";
      this.getRequest().setAttribute("calcoloEseguito", "1");


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
