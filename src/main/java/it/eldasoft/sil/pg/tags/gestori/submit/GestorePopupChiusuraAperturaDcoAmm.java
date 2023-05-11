/*
 * Created on 21/10/15
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

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Gestore non standard per la pagina popupChiusuraAperturaDocAmm.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupChiusuraAperturaDcoAmm extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupChiusuraAperturaDcoAmm.class);

	@Override
  public String getEntita() {
		return "GARE";
	}

  public GestorePopupChiusuraAperturaDcoAmm() {
	    super(false);
	  }

	/**
	 * @param isGestoreStandard
	*/
  public GestorePopupChiusuraAperturaDcoAmm(boolean isGestoreStandard) {
	    super(isGestoreStandard);
	}

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  String operazione =UtilityStruts.getParametroString(this.getRequest(),"operazione");
      operazione = UtilityStringhe.convertiNullInStringaVuota(operazione);
      String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
      String isGaraLottiConOffertaUnica = UtilityStruts.getParametroString(this.getRequest(),"isGaraLottiConOffertaUnica");
      String bustalotti = UtilityStruts.getParametroString(this.getRequest(),"bustalotti");
      Long fasgar=null;
      Long stepgar=null;
      String paginaAttivaWizard = UtilityStruts.getParametroString(this.getRequest(),"paginaAttivaWizard");
      String faseDaImpostare = UtilityStruts.getParametroString(this.getRequest(),"faseDaImpostare");

      //variabili per tracciatura eventi
      String messageKey = null;
      int livEvento = 3;
      String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
      try{
        try{
          if("CALCOLO".equals(faseDaImpostare) && "2".equals(bustalotti)){
            fasgar= new Long(7);
            stepgar= new Long(70);
          }else if("ECO".equals(faseDaImpostare)){
            fasgar= new Long(6);
            stepgar= new Long(60);
          } else if("ATTIVA".equals(operazione) && !"true".equals(isGaraLottiConOffertaUnica)){
            Vector dati = this.sqlManager.getVector("select g.modlicg, g1.valtec from gare g,gare1 g1 where g.ngara=? and g.ngara=g1.ngara", new Object[]{ngara});
            if(dati!=null && dati.size()>0){
              Long modlicg = SqlManager.getValueFromVectorParam(dati, 0).longValue();
              String valtec = SqlManager.getValueFromVectorParam(dati, 1).getStringValue();
              if("1".equals(valtec) || (modlicg!=null && modlicg.longValue()==6)){
                fasgar= new Long(5);
                stepgar= new Long(50);
              }else{
                fasgar= new Long(6);
                stepgar= new Long(60);
              }
            }
          }else  if("ATTIVA".equals(operazione) && "true".equals(isGaraLottiConOffertaUnica) && "2".equals(bustalotti)){
            if("TEC".equals(faseDaImpostare)){
              fasgar= new Long(5);
              stepgar= new Long(50);
            }else{
              fasgar = new Long(7);
              stepgar = new Long(70);
            }
          }else if("ATTIVA".equals(operazione) && "true".equals(isGaraLottiConOffertaUnica) && "1".equals(bustalotti)){
            Long conteggio = (Long)this.sqlManager.getObject("select count(g.ngara) from gare g, gare1 g1 where g.codgar1=? and g.ngara = g1.ngara "
                + "and g.ngara!=g.codgar1 and (g.modlicg = ? or g1.valtec=?)", new Object[]{ngara, new Long(6), "1"});
            if(conteggio!=null && conteggio.longValue()>0){
              fasgar = new Long(5);
              stepgar = new Long(50);
            }else{
              fasgar = new Long(6);
              stepgar = new Long(60);
            }
          }else if("DISATTIVA".equals(operazione)){
            fasgar= new Long(2);
            stepgar= new Long(35);
          }
          if("2".equals(bustalotti) && ("TEC".equals(faseDaImpostare) || "ECO".equals(faseDaImpostare))){
            this.sqlManager.update("update gare set fasgar=?, stepgar=? where codgar1=?", new Object[]{fasgar, stepgar,ngara});
          }else{
            this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{fasgar, stepgar,ngara});
            //Nel caso di attivazione dell'offerta economica per bustalotti=1 si deve aggiornare lo stato della gara fittizia, ma se questa ha uno stato < 6
            if("ECO".equals(faseDaImpostare) && "1".equals(bustalotti)){
              Long fasgarGara = (Long)this.sqlManager.getObject("select fasgar from gare where ngara=(select codgar1 from gare where ngara=?)", new Object[]{ngara});
              if(fasgarGara==null || (fasgarGara!=null && fasgarGara.longValue()<6))
                this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=(select codgar1 from gare where ngara=?)", new Object[]{fasgar, stepgar,ngara});
            }
          }

          if(("ATTIVA".equals(operazione) || "ECO".equals(faseDaImpostare)) && fasgar != null && (fasgar.longValue()==5 || fasgar.longValue() ==6)){
            MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
                this.getServletContext(), MEPAManager.class);
            if("true".equals(isGaraLottiConOffertaUnica) && "1".equals(bustalotti)){
              List listaLotti = this.sqlManager.getListVector("select ngara from gare where codgar1=? and ngara!=codgar1", new Object[]{ngara});
              if(listaLotti!=null && listaLotti.size()>0){
                String lotto = null;
                for(int i=0;i<listaLotti.size();i++){
                  lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
                  //Nel caso di fase economica si deve specificare che siamo nel caso di lotto
                  mepaManager.impostaComunicazioniAScartate(lotto,"OFFERTE_ECO_LOTTI_DISTINTI");
                  mepaManager.impostaComunicazioniDaRiaquisireLottoPlicoUnicoOfferteDistinte(lotto);

                }
              }
            }else{
              String tipo="OFFERTE";
              if("1".equals(bustalotti)){
                //Nel caso di bustalotti=1 si deve specificare che siamo nel caso di lotto
                tipo="OFFERTE_ECO_LOTTI_DISTINTI";
              }
              mepaManager.impostaComunicazioniAScartate(ngara,tipo);
              if("1".equals(bustalotti))
                mepaManager.impostaComunicazioniDaRiaquisireLottoPlicoUnicoOfferteDistinte(ngara);
              else
                mepaManager.impostaComunicazioniDaRiaquisire(ngara);

            }
          }

          //Aggiornamento della fase dei lotti nel caso di bustalotti = 1
          if("ATTIVA".equals(operazione) && "true".equals(isGaraLottiConOffertaUnica) && "1".equals(bustalotti) && faseDaImpostare==null){
            //per ogni lotto devo leggere il gare.modlicg e gare1.valtec
            List listaLotti = this.sqlManager.getListVector("select g.ngara, g.modlicg, g1.valtec from gare g, gare1 g1 where g.codgar1=? and g.ngara!=g.codgar1 and g.ngara=g1.ngara", new Object[]{ngara});
            if(listaLotti!=null && listaLotti.size()>0){
              String lotto = null;
              Long modlicg = null;
              String valtec = null;
              for(int i=0;i<listaLotti.size();i++){
                lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
                modlicg = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).longValue();
                valtec = SqlManager.getValueFromVectorParam(listaLotti.get(i), 2).getStringValue();
                if ("1".equals(valtec) || (new Long(6)).equals(modlicg)){
                  fasgar = new Long(5);
                  stepgar = new Long(50);
                }else{
                  fasgar = new Long(6);
                  stepgar = new Long(60);
                }
                this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{fasgar, stepgar,lotto});
              }
            }
          }

          livEvento = 1;
          errMsgEvento = "";

          if("DISATTIVA".equals(operazione))
          //si devono sbiancare fasgar e stepgar dei lotti
            this.sqlManager.update("update GARE set FASGAR = null, STEPGAR = null where CODGAR1=? and NGARA != CODGAR1 ", new Object[] {ngara});

        }catch(SQLException e) {
          livEvento = 3;
          errMsgEvento = "Errore durante l'operazione di attivazione\\disattivazione dell'apertura offerte";
          this.getRequest().setAttribute("operazioneEseguita", "ERRORI");
          throw new GestoreException(
                  "Errore durante l'operazione di attivazione\\disattivazione dell'apertura offerte)",
                 null, e);
        }

      }finally{
        if(!"DISATTIVA".equals(operazione)){
          String codEvento = "GA_ATTIVA_FASE_OFFERTE";
          String descrEvento = "Attivazione fase apertura offerte";
          if("ECO".equals(faseDaImpostare)){
            codEvento = "GA_ATTIVA_FASE_OFFERTE_ECO";
            descrEvento = "Attivazione fase apertura offerte economiche";
          }else if("TEC".equals(faseDaImpostare)){
            codEvento = "GA_ATTIVA_FASE_OFFERTE_TEC";
            descrEvento = "Attivazione fase apertura offerte tecniche";
          }else if(faseDaImpostare == null && "true".equals(isGaraLottiConOffertaUnica) && "2".equals(bustalotti) ){
            codEvento = "GA_ATTIVA_FASE_CALCOLOAGG";
            descrEvento = "Attivazione fase calcolo aggiudicazione";
          }
          //Tracciatura eventi
          try {
            LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
            logEvento.setLivEvento(livEvento);
            logEvento.setOggEvento(ngara);
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

      this.getRequest().setAttribute("operazioneEseguita", "OK");
      this.getRequest().setAttribute("operazione", operazione);
      this.getRequest().setAttribute("bustalotti", bustalotti);
      this.getRequest().setAttribute("faseDaImpostare", faseDaImpostare);
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}
}
