/*
 * Created on 21/11/16
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard per la popup della conclusione dell'asta
 *
 * @author Marcello Caminiti
 */
public class GestorePopupConcludiAsta extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupConcludiAsta.class);

  /** Manager per la gestione delle chiavi. */
  private AggiudicazioneManager aggiudicazioneManager;

  private PgManager pgManager;

  private PgManagerEst1 pgManagerEst1;


  public GestorePopupConcludiAsta() {
    super(false);
  }


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per generare le chiavi
    aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean("aggiudicazioneManager",
        this.getServletContext(), AggiudicazioneManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);


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
    String codEvento = "GA_CONCLUDI_ASTA";
    String oggEvento = "";
    String descrEvento = "Conclusione asta elettronica ";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String messageKey = "";

    try{
      // lettura dei parametri di input

      String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
      oggEvento = ngara;

      try {
        Long modlicg = null;
        Long ribcal =  null;
        Double impapp = null;
        Double onprge = null;
        Double impsic = null;
        Double impnrl = null;
        String sicinc = null;
        String onsogrib = null;
        boolean gcapPopolata = false;
        String codicegara = null;


        Vector datiGara = this.sqlManager.getVector("select modlicg, ribcal, impapp, onprge, impsic, impnrl, sicinc, onsogrib,codgar1 from gare where ngara=?", new Object[]{ngara});
        if(datiGara != null && datiGara.size()>0){
          modlicg = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
          ribcal = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
          impapp = SqlManager.getValueFromVectorParam(datiGara, 2).doubleValue();
          onprge = SqlManager.getValueFromVectorParam(datiGara, 3).doubleValue();
          impsic = SqlManager.getValueFromVectorParam(datiGara, 4).doubleValue();
          impnrl = SqlManager.getValueFromVectorParam(datiGara, 5).doubleValue();
          sicinc = SqlManager.getValueFromVectorParam(datiGara, 6).stringValue();
          onsogrib = SqlManager.getValueFromVectorParam(datiGara, 7).stringValue();
          codicegara = SqlManager.getValueFromVectorParam(datiGara, 8).stringValue();

          if (impapp == null)
              impapp = new Double(0);
          if (impnrl == null)
              impnrl = new Double(0);
          if (impsic == null)
              impsic = new Double(0);
          if (onprge == null)
            onprge = new Double(0);
        }
        String cifreRibasso=this.pgManagerEst1.getNumeroDecimaliRibasso(codicegara);

        if(new Long(5).equals(modlicg) || new Long(14).equals(modlicg)){
          Long conteggioGcap = (Long)this.sqlManager.getObject("select count(ngara) from gcap where ngara=?", new Object[]{ngara});
          if(conteggioGcap!=null && conteggioGcap.longValue()>0)
            gcapPopolata=true;
        }
        List listaRilanci = this.sqlManager.getListVector("select id, dittao, impoff, ribauo from aerilanci a where ngara=? and numril <> -1 "
            + "and numril = (select max(numril) from aerilanci b where a.ngara=b.ngara and a.dittao=b.dittao)", new Object[]{ngara});
        if(listaRilanci!=null && listaRilanci.size()>0 && (new Long(5).equals(modlicg) || new Long(14).equals(modlicg) || new Long(1).equals(modlicg) || new Long(13).equals(modlicg))){
          Long idRil = null;
          String ditta = null;
          Double impoff = null;
          Double ribauo = null;
          for(int i=0;i<listaRilanci.size();i++){
            idRil = SqlManager.getValueFromVectorParam(listaRilanci.get(i), 0).longValue();
            ditta = SqlManager.getValueFromVectorParam(listaRilanci.get(i), 1).stringValue();
            impoff= SqlManager.getValueFromVectorParam(listaRilanci.get(i), 2).doubleValue();
            ribauo = SqlManager.getValueFromVectorParam(listaRilanci.get(i), 3).doubleValue();

            if((new Long(5).equals(modlicg) || new Long(14).equals(modlicg) || new Long(1).equals(modlicg) || new Long(13).equals(modlicg)) && new Long(2).equals(ribcal)){
              if (ribauo == null)
                ribauo = new Double(0);
              if (impoff == null)
                impoff = new Double(0);
              ribauo = this.aggiudicazioneManager.calcolaRIBAUO(impapp.doubleValue(), onprge.doubleValue(), impsic.doubleValue(), impnrl.doubleValue(), sicinc, impoff.doubleValue(), onsogrib);
              if(cifreRibasso!=null && !"".equals(cifreRibasso)){
                ribauo = (Double)UtilityNumeri.arrotondaNumero(ribauo, new Integer(cifreRibasso));
              }
            }else if((new Long(5).equals(modlicg) || new Long(14).equals(modlicg)) && new Long(1).equals(ribcal) && impoff==null && ribauo!=null){
              impoff = pgManager.calcolaImportoOfferto(impapp, impsic, impnrl, onprge, ribauo, sicinc, onsogrib);
              impoff = (Double)UtilityNumeri.arrotondaNumero(impoff, new Integer(5));
            }
            this.sqlManager.update("update ditg set ribauo = ?, impoff = ?, isrilancio = '1' where ngara5=? and dittao=?",new Object[]{ribauo, impoff, ngara, ditta});

            if(gcapPopolata){
              List listaOfferteRilanci = this.sqlManager.getListVector("select contaf, preoff, impoff from aerilpre where ngara=? and dittao=? and idril=?", new Object[]{ngara,ditta,idRil});
              if(listaOfferteRilanci!=null && listaOfferteRilanci.size()>0){
                for(int j=0;j<listaOfferteRilanci.size();j++){
                  Long contaf = SqlManager.getValueFromVectorParam(listaOfferteRilanci.get(j), 0).longValue();
                  Double preoff = SqlManager.getValueFromVectorParam(listaOfferteRilanci.get(j), 1).doubleValue();
                  Double importoOfferto = SqlManager.getValueFromVectorParam(listaOfferteRilanci.get(j), 2).doubleValue();
                  Long conteggioDPRE =  (Long)this.sqlManager.getObject("select count(ngara) from dpre where ngara=? and contaf=?", new Object[]{ngara,contaf});
                  if(conteggioDPRE!=null && conteggioDPRE.longValue()>0){
                    this.sqlManager.update("update dpre set preoff = ?, impoff = ? where ngara=? and dittao=? and contaf=?",new Object[]{preoff, importoOfferto, ngara, ditta,contaf});
                  }else{
                    this.getRequest().setAttribute("conclusioneEseguita", "-1");
                    livEvento = 3;
                    errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.astaElettronica.Conclusione.OffertaNonPresente");
                    throw new GestoreException("Errore nell'aggiornamento dei prezzi unitari della gara", "astaElettronica.Conclusione.OffertaNonPresente", new Exception());
                  }
                }

              }
            }
          }
        }

        this.sqlManager.update("update GARE set STEPGAR = 70 where NGARA=?",new Object[]{ngara});
      } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = "Errore nell'aggiornamento della gara";
        throw new GestoreException("Errore nell'aggiornamento della gara", null, e);
      }

      livEvento = 1;
      errMsgEvento = "";
      // setta l'operazione a completata, in modo da scatenare il reload della
      // pagina principale
      this.getRequest().setAttribute("conclusioneEseguita", "1");

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
