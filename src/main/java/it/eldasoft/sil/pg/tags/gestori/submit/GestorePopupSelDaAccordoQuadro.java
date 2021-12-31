/*
 * Created on 03/08/10
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
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard
 *
 * @author Marcello Caminiti
 */
public class GestorePopupSelDaAccordoQuadro extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "GCAP";
  }

  public GestorePopupSelDaAccordoQuadro() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupSelDaAccordoQuadro(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {


    String numeroGara=this.getRequest().getParameter("ngara");
    String aqoper = this.getRequest().getParameter("aqoper");
    String ngaraaq = this.getRequest().getParameter("ngaraaq");
    String ribcal = this.getRequest().getParameter("ribcal");
    String insert = "insert into gcap(ngara,contaf,contafaq,norvoc,codvoc,voce,codcat,clasi1,solsic,sogrib,unimis,quanti,prezun,perciva,peso) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    int numeroLavorazioni = 0;
    String numLavorazioni = this.getRequest().getParameter("numeroLavorazioni");
    if(numLavorazioni != null && numLavorazioni.length() > 0)
      numeroLavorazioni =  UtilityNumeri.convertiIntero(numLavorazioni).intValue();

    String[] listaLavorazioniSelezionate = this.getRequest().getParameterValues("keys");

    String campoContaf=null;
    String campoCodvoc=null;
    String campoVoce=null;
    String campoCodcat=null;
    String campoClasi1=null;
    String campoSolsic=null;
    String campoSogrib=null;
    String campoUnimis=null;
    String campoquanti=null;
    String campoPreoff=null;
    String campoPerciva=null;
    String campoPeso=null;

    if("1".equals(aqoper)){
      campoContaf="V_GCAP_DPRE.CONTAF";
      campoCodvoc = "CODVOC_FIT";
      campoVoce = "VOCE_FIT";
      campoCodcat = "GCAP.CODCAT";
      campoClasi1 = "GCAP.CLASI1";
      campoSolsic = "V_GCAP_DPRE.SOLSIC";
      campoSogrib = "V_GCAP_DPRE.SOGRIB";
      campoUnimis = "UNIMIS_FIT";
      campoquanti = "V_GCAP_DPRE.QUANTIEFF";
      campoPreoff = "PREOFF_FIT";
      campoPerciva = "V_GCAP_DPRE.PERCIVAEFF";
      campoPeso = "V_GCAP_DPRE.PESO";
    }else{
      campoContaf="GCAP.CONTAF";
      campoCodvoc = "CODVOC_FIT";
      campoVoce = "VOCE_FIT";
      campoCodcat = "GCAP.CODCAT";
      campoClasi1 = "GCAP.CLASI1";
      campoSolsic = "GCAP.SOLSIC";
      campoSogrib = "GCAP.SOGRIB";
      campoUnimis = "UNIMIS_FIT";
      campoquanti = "GCAP.QUANTI";
      campoPreoff = "PREZUN_FIT";
      campoPerciva = "GCAP.PERCIVA";
      campoPeso = "GCAP.PESO";
    }

    Long contaf=null;
    boolean lavorazioneSel = false;
    Long maxContaf= null;
    Object norvoc = null;
    Double maxNorvoc = null;
    for (int i = 1; i <= numeroLavorazioni; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
          impl.getColumnsBySuffix("_" + i, false));
      contaf = dataColumnContainerDiRiga.getLong(campoContaf);
      lavorazioneSel = false;

      for (int j = 0; j < listaLavorazioniSelezionate.length; j++) {
        String contafString = listaLavorazioniSelezionate[j];
        Long contafSel = new Long(contafString);
        if(contafSel.equals(contaf)){
          lavorazioneSel= true;
          break;
        }

      }

      if(lavorazioneSel){

        String codvoc = dataColumnContainerDiRiga.getString(campoCodvoc);
        String voce = dataColumnContainerDiRiga.getString(campoVoce);
        String codcat = dataColumnContainerDiRiga.getString(campoCodcat);
        Long clasi1 = dataColumnContainerDiRiga.getLong(campoClasi1);
        String solsic = dataColumnContainerDiRiga.getString(campoSolsic);
        String sogrib = dataColumnContainerDiRiga.getString(campoSogrib);
        String unimis = dataColumnContainerDiRiga.getString(campoUnimis);
        Double quanti = dataColumnContainerDiRiga.getDouble(campoquanti);
        Double preoff = dataColumnContainerDiRiga.getDouble(campoPreoff);
        Long perciva = dataColumnContainerDiRiga.getLong(campoPerciva);
        Double peso =null;
        if("3".equals(ribcal))
          peso = dataColumnContainerDiRiga.getDouble(campoPeso);

        try {
          String desest = (String)this.sqlManager.getObject("select desest from gcap_est where contaf=? and ngara=?", new Object[]{contaf,ngaraaq});
          maxContaf = (Long)this.sqlManager.getObject("select max(contaf) from gcap where ngara=?", new Object[]{numeroGara});
          if(maxContaf==null)
            maxContaf =  new Long(1);
          else
            maxContaf = new Long(maxContaf.longValue()+1);

          norvoc = this.sqlManager.getObject("select max(norvoc) from gcap where ngara=?", new Object[]{numeroGara});
          if(norvoc==null)
            maxNorvoc =  new Double(1);
          else{
            if(norvoc instanceof Double)
              maxNorvoc = new Double(((Double)norvoc).doubleValue()+1);
            else
              maxNorvoc = new Double(((Long)norvoc).longValue()+1);
          }

          this.getSqlManager().update(insert, new Object[] { numeroGara,maxContaf,contaf,maxNorvoc, codvoc,voce,codcat,clasi1,solsic,sogrib,unimis,quanti,preoff,perciva,peso});
          if(desest!=null)
            this.getSqlManager().update("insert into GCAP_EST (NGARA, CONTAF, DESEST) values (?, ?, ?) ", new Object[] { numeroGara,maxContaf,desest});

        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento dei dati della lavorazione dell'accordo quadro in GCAP",null, e);
        }

      }

    }

    if("1".equals(aqoper)){
      PgManager pgManager = (PgManager)UtilitySpring.getBean("pgManager", this.getServletContext(),PgManager.class);
      pgManager.aggiornamentoImportiDaLavorazioni(numeroGara, true);
    }
    this.getRequest().setAttribute("InserimentoEseguito", "SI");
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}