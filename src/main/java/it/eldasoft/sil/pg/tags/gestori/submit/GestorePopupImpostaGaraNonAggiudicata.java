/*
 * Created on 11/10/12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per valorizzare il campo GARE.ESINEG
 *
 * @author Marcello.Caminiti
 */
public class GestorePopupImpostaGaraNonAggiudicata extends AbstractGestoreEntita {

  /** Manager Integrazione WSERP */
  private GestioneWSERPManager gestioneWSERPManager;

  public GestorePopupImpostaGaraNonAggiudicata() {
    super(false);
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
    // lettura dei parametri di input
    String ngara = datiForm.getString("NGARA");
    String codgar = datiForm.getString("CODGARA");
    String esinegString = datiForm.getString("ESINEG");
    String isLottoOffDistinte = datiForm.getString("ISLOTTOOFFDISTINTE");
    String isOffertaUnica = datiForm.getString("ISOFFERTAUNICA");
    Date datneg = datiForm.getData("DATNEG");
    String notneg = datiForm.getString("NOTNEG");
    String npannrevagg = null;
    if(datiForm.isColumn("NPANNREVAGG")){
      npannrevagg = datiForm.getString("NPANNREVAGG");
    }

    String isLottoOffunica = datiForm.getString("ISLOTTOOFFUNICA");
    Long esineg=null;
    if(esinegString!=null && !"".equals(esinegString))
      esineg = Long.parseLong(esinegString);

    String isOfferteDistinte = datiForm.getString("ISOFFERTEDISTINTE");

    try {
      if("Si".equals(isOfferteDistinte)){
        this.getSqlManager().update(
            "update torn set esineg=?, datneg=? where codgar = ?",
            new Object[] { esineg, datneg, codgar });
      }else{
        this.getSqlManager().update(
            "update gare set esineg=?, datneg=? where NGARA = ?",
            new Object[] { esineg, datneg, ngara });

        this.getSqlManager().update(
            "update gare1 set notneg=?, npannrevagg=? where NGARA = ?",
            new Object[] { notneg,npannrevagg, ngara});
      }

      //Nel caso di lotti di gara per gare ad offerte distinte si deve aggiornare anche TORN.ESINEG
      //Se lotto di gara ad offerta unica si aggiorna GARe.ESINEG della gara complementare
      if("Si".equals(isLottoOffDistinte) || "Si".equals(isLottoOffunica)){
        Long esinegTorn = esineg;
        Date datnegTorn = datneg;
        boolean sbiancamento = false;
        String select ="select esineg from gare where codgar1=? and ngara <> ?";
        if("Si".equals(isLottoOffunica))
          select += " and ngara!=codgar1";
        List esinegLotti = sqlManager.getListVector(select, new Object[]{codgar,ngara});
        if(esinegLotti!= null && esinegLotti.size()>0){
          for(int i=0;i<esinegLotti.size();i++){
            Long esinegLotto = SqlManager.getValueFromVectorParam(esinegLotti.get(i), 0).longValue();
            if (esinegLotto== null || (esinegLotto!= null && esineg!=null && !esinegLotto.equals(esineg))){
              esinegTorn=null;
              datnegTorn = null;
              sbiancamento = true;
              break;
            }
          }
        }

        select = "update torn set esineg=?, datneg=? where codgar = ?";
        if("Si".equals(isLottoOffunica))
          select = "update gare set esineg=?, datneg=? where ngara = ?";
        this.getSqlManager().update(
            select, new Object[] { esinegTorn,datnegTorn, codgar});
        //Nel caso di lotto offerta unica si deve sbiancare l'occorrenza di Gare1 associata alla gara fittizia
        if("Si".equals(isLottoOffunica) && (sbiancamento || esinegTorn==null)){
          this.getSqlManager().update(
              "update gare1 set notneg=null, npannrevagg=null where NGARA = ?",
              new Object[] { codgar});
        }

      }

      // Nel caso di gara a lotti con offerta unica bisogna aggiornare il
      // FASGAR dell'occorrenza complementare solo se tutti i lotti
      // sono aggiudicati definitavamente o ESINEG >0 si imposta la fase a 8,
      //altrimenti a 7
      if ("Si".equals(isLottoOffunica)) {
          Long numeroLotti = (Long) this.sqlManager.getObject(
              "select count(*) from GARE where CODGAR1 = ? and GENERE is null",
              new Object[] { codgar });
          Long numeroLottiAggiudicatiDef = (Long) this.sqlManager.getObject(
              "select count(*) from GARE where CODGAR1 = ? and GENERE is null "
                      + "and (FASGAR = 8 or ESINEG > 0)", new Object[] { codgar });

          Long fasgar=null;
          if (numeroLotti != null && numeroLottiAggiudicatiDef != null &&
              (numeroLotti.longValue() == numeroLottiAggiudicatiDef.longValue())){
            fasgar = new Long(GestioneFasiGaraFunction.FASE_AGGIUDICAZIONE_DEFINITIVA/10);
          }else{
            fasgar = new Long(GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE/10);
          }
          this.sqlManager.update("update gare set fasgar =? where ngara=? ", new Object[]{fasgar, codgar});
      }

      //Nel caso di gara ad offerta unica si allineano i campi ESINEG e DATNEG dei lotti con quello della gara fittizia
      if("Si".equals(isOffertaUnica) || "Si".equals(isOfferteDistinte)){
        this.getSqlManager().update(
            "update gare set esineg=?, datneg = ? where codgar1 = ? and ngara!=codgar1",
            new Object[] { esineg,datneg,codgar});
        String updateGare1 = "update gare1 set notneg=?, npannrevagg=? where codgar1 = ? and ngara!=codgar1";
        this.getSqlManager().update(updateGare1,new Object[]{notneg,npannrevagg, codgar});
      }

      //Aggiornamento della data ultimo aggiornamento solo se ESINEG e DATNEG sono stati realmente modificati rispetto ai
      //valori nella banca dati.
      //Nel caso di lotti per aggiornare la data si controlla la variazione del lotto e non la condizione della gara.
      if(datiForm.isModifiedColumn("ESINEG") || datiForm.isModifiedColumn("DATNEG")){
        PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
            this.getServletContext(), PgManagerEst1.class);
        String pubblicazioneBandoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "BANDO", false);
        String pubblicazioneEsitoPortale = "FASLE";
        if("Si".equals(isLottoOffDistinte) || "Si".equals(isOfferteDistinte) || "Si".equals(isLottoOffunica) || "Si".equals(isOffertaUnica)){
          pubblicazioneEsitoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "ESITO", true);
        }else{
          pubblicazioneEsitoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(ngara, "ESITO", false);
        }
        if("TRUE".equals(pubblicazioneBandoPortale) || "TRUE".equals(pubblicazioneEsitoPortale)){
          Date oggi = UtilityDate.getDataOdiernaAsDate();
          this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{oggi,codgar});
        }
      }

      //Aggiornamento di APPA.FASEAPPALTO
      //if(!"Si".equals(isOffertaUnica) && !"Si".equals(isLottoOffunica)){
        String select="select clavor, numera, esineg, ngara from gare where codgar1='" + codgar + "'";
        if("Si".equals(isLottoOffDistinte) || codgar.startsWith("$") || "Si".equals(isLottoOffunica)){
          select += " and ngara='" + ngara + "'";
        }
        List lotti= this.sqlManager.getListVector(select, null);
        if(lotti!=null && lotti.size()>0){
          String clavor=null;
          Long numera=null;
          Long esinegLotto=null;
          String valoreAppa=null;
          Long numeroLottiAppalto=null;
          String codiceLotto = null;
          for(int i=0; i< lotti.size();i++){
            clavor=SqlManager.getValueFromVectorParam(lotti.get(i), 0).stringValue();
            numera=SqlManager.getValueFromVectorParam(lotti.get(i), 1).longValue();
            codiceLotto = SqlManager.getValueFromVectorParam(lotti.get(i), 3).stringValue();
            if(numera!=null){
              esinegLotto = SqlManager.getValueFromVectorParam(lotti.get(i), 2).longValue();
              if(esinegLotto!=null && !"".equals(esinegLotto)){
                valoreAppa="P";
                //Ripristina il campo FASEAPPALTO a 'P' solo se è non ci sono altre gare/lotti che fanno riferimento allo stesso appalto (caso di lotto di gara con plico unico)
                numeroLottiAppalto= (Long)this.sqlManager.getObject("select count(ngara) from gare where clavor=? and numera=? and esineg is null", new Object[]{clavor,numera});
              } else {
                valoreAppa="A";
              }
              if(numeroLottiAppalto==null || (numeroLottiAppalto!=null && numeroLottiAppalto.longValue()==0)){
                this.sqlManager.update("update appa set faseappalto=? where codlav=? and nappal=?", new Object[]{valoreAppa,clavor,numera});
              }
            }
            //Integrazione con WSERP
            String urlWSERP = ConfigManager.getValore("wserp.erp.url");
            if(urlWSERP != null && !"".equals(urlWSERP)){
              if(esineg != null){
                gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
                    this.getServletContext(), GestioneWSERPManager.class);
                WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
                if(configurazione.isEsito()){
                  String tipoWSERP = configurazione.getRemotewserp();


                  if("SMEUP".equals(tipoWSERP)){
                    int res = this.gestioneWSERPManager.scollegaRda(codgar, codiceLotto, "2", null, null, this.getRequest());
                    if(res < 0){
                      throw new GestoreException(
                          "Errore durante l'operazione di scollegamento delle RdA dalla gara",
                          "scollegaRdaGara", null);
                    }
                  }else if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
                    //nel caso di FNM non si scollega
                    Long countGareRda = (Long)this.sqlManager.getObject("select count(numrda) from garerda where codgar = ? and numrda is not null", new Object[]{codgar});
                    String linkrda = null;
                    if(countGareRda >0 ){
                      linkrda = "1";
                    }else{
                      linkrda = "2";
                    }
                    int res = this.gestioneWSERPManager.scollegaRda(codgar, codiceLotto, linkrda, null, null, this.getRequest());
                    if(res < 0){
                      throw new GestoreException(
                          "Errore durante l'operazione di scollegamento delle RdA dalla gara",
                          "scollegaRdaGara", null);
                    }
                  }
                }
              }
            }
          }
        }
      //}
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'aggiornamento del campo GARE.ESINEG",null,  e);
    }

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("esito", "1");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
