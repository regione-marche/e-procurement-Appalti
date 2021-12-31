/*
 * Created on 02-dic-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per update dei dati della seconda fase della pagina Aggiudicazione provvisoria.
 * La pagina anche se contiene dati di GARE è definità sull'entità TORN
 * perchè altrimenti al salvataggio si perderebbe la chiave di TORN
 *
 * @author Marcello caminiti
 */
public class GestoreAggProvvOffertaUnica extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "TORN";
  }

  public GestoreAggProvvOffertaUnica() {
    super(false);
  }


  public GestoreAggProvvOffertaUnica(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }


  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    DefaultGestoreEntita gestoreGARE = new DefaultGestoreEntita("GARE",
        this.getRequest());

    // Aggiornamento dei campi di gare
    gestoreGARE.update(status, datiForm);

    if(datiForm.isColumn("GARE1.NPLETTAGGPROVV") || datiForm.isColumn("GARE1.DCOMSVIP")){
      DefaultGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1",
          this.getRequest());
      gestoreGARE1.update(status, datiForm);
    }

    //Aggiornamento del campo GARE.DATTOA dei lotti di una gara ad offerta unica con il valore di
    //GARE.DATTOA della gara fittizia
    String aggiudDef =  UtilityStruts.getParametroString(this.getRequest(),"aggiudDef");
    if("Si".equals(aggiudDef)){
      String codgar = datiForm.getString("TORN.CODGAR");
      Date dattoa = datiForm.getData("GARE.DATTOA");
      Long tattoa = datiForm.getLong("GARE.TATTOA");
      String nattoa = datiForm.getString("GARE.NATTOA");
      //aggiornamento del numero di aggiudicazione di tutti i lotti, se si sbianca la dattoa si decrementa, se invece
      //si valorizza si incrementa.

      try {
        String elencoe = datiForm.getString("GARE.ELENCOE");
        if(elencoe!=null && !"".equals(elencoe) && datiForm.isModifiedColumn("GARE.DATTOA")){
          String codiceElenco="$"+elencoe;
          Long tipoalgo = (Long)sqlManager.getObject("select tipoalgo from garealbo where ngara=?", new Object[]{elencoe});
          Long tipgen = datiForm.getLong("TORN.TIPGEN");
          Vector datiCatg = this.sqlManager.getVector("select catiga, numcla from catg where ngara=? and ncatg=1", new Object[]{codgar});
          String catiga=null;
          Long numcla=null;
          if(datiCatg!=null && datiCatg.size()>0){
            catiga = (String)((JdbcParametro) datiCatg.get(0)).getValue();
            numcla = (Long)((JdbcParametro) datiCatg.get(1)).getValue();
          }
          String stazioneAppaltante = (String)this.sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codgar});
          String modo=null;
          Timestamp dattoaOrig = datiForm.getColumn("GARE.DATTOA").getOriginalValue().dataValue();
          if(dattoaOrig==null && dattoa!=null)
            modo="INS";
          else if(dattoaOrig!=null && dattoa==null)
            modo="DEL";
          if(modo!=null)
            pgManager.aggiornaNumAggiudicazioniLotti(codiceElenco, elencoe, codgar, null, catiga, tipgen, numcla, stazioneAppaltante, tipoalgo, modo);
        }
        this.getSqlManager().update(
            "update gare set dattoa=?, tattoa = ?, nattoa =? where codgar1 = ? and ngara!=codgar1",
            new Object[] { dattoa,tattoa,nattoa,codgar});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante l'aggiornamento del campo GARE.DATTOA dei lotti della gara:" + codgar,null,  e);
      }
    }

    //Gestione personalizzata delle sezioni dinamiche per gli atti di gara (aggiudicazione)
    AbstractGestoreChiaveNumerica gestoreGAREATTI = new DefaultGestoreEntitaChiaveNumerica(
        "GAREATTI", "ID", new String[] {}, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreGAREATTI, "GAREATTIAGG",
        new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);

    if((datiForm.isColumn("GARESTATI.NPLETTCOMESCLOFEC") && datiForm.isModifiedColumn("GARESTATI.NPLETTCOMESCLOFEC")) || (datiForm.isColumn("GARESTATI.DPLETTCOMESCLOFEC") && datiForm.isModifiedColumn("GARESTATI.DPLETTCOMESCLOFEC"))){

      // Aggiornamento dei campi di GARESTATI
      DefaultGestoreEntita gestoreGARESTATI = new DefaultGestoreEntita(
          "GARESTATI", this.getRequest());

      String codiceTornata = datiForm.getString("GARE.CODGAR1");
      String numeroGara = datiForm.getString("GARE.NGARA");

      //Nel caso siano stati modificati NPLETTCOMESCLOFTE e DPLETTCOMESCLOFTE fasgar=5 e stepgar=5
      //Nel caso siano stati modificati NPLETTCOMESCLOFEC e DPLETTCOMESCLOFEC fasgar=6 e stepgar=6
      Long faseGaraEc = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE/10);
      Long stepGaraEc = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE);


      DataColumn dataColumnGareStati[] = new DataColumn[6];
      dataColumnGareStati[0] = new DataColumn("GARESTATI.CODGAR",new JdbcParametro(JdbcParametro.TIPO_TESTO,null));
      dataColumnGareStati[1] = new DataColumn("GARESTATI.NGARA",new JdbcParametro(JdbcParametro.TIPO_TESTO,null));
      dataColumnGareStati[2] = new DataColumn("GARESTATI.FASGAR",new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
      dataColumnGareStati[3] = new DataColumn("GARESTATI.STEPGAR",new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
      dataColumnGareStati[4] = new DataColumn("GARESTATI.NPLETTCOMESCL",new JdbcParametro(JdbcParametro.TIPO_TESTO,null));
      dataColumnGareStati[5] = new DataColumn("GARESTATI.DPLETTCOMESCL",new JdbcParametro(JdbcParametro.TIPO_DATA,null));
      DataColumnContainer dataColumnContainerGARESTATI = new DataColumnContainer(dataColumnGareStati);

      dataColumnContainerGARESTATI.setValue("GARESTATI.CODGAR", codiceTornata);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.CODGAR").setObjectOriginalValue(codiceTornata);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.CODGAR").setChiave(true);

      dataColumnContainerGARESTATI.setValue("GARESTATI.NGARA", numeroGara);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.NGARA").setObjectOriginalValue(numeroGara);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.NGARA").setChiave(true);

      Date datamtp = new Date(1);
      datamtp.setTime(1);


      dataColumnContainerGARESTATI.setValue("GARESTATI.FASGAR", faseGaraEc);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.FASGAR").setObjectOriginalValue(faseGaraEc);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.FASGAR").setChiave(true);

      dataColumnContainerGARESTATI.setValue("GARESTATI.STEPGAR", stepGaraEc);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.STEPGAR").setObjectOriginalValue(stepGaraEc);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.STEPGAR").setChiave(false);

      String NPLETTCOMESCLOFEC = StringUtils.stripToNull(datiForm.getString("GARESTATI.NPLETTCOMESCLOFEC"));
      dataColumnContainerGARESTATI.setValue("GARESTATI.NPLETTCOMESCL", NPLETTCOMESCLOFEC);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.NPLETTCOMESCL").setObjectOriginalValue(" ");
      dataColumnContainerGARESTATI.getColumn("GARESTATI.NPLETTCOMESCL").setChiave(false);

      Timestamp DPLETTCOMESCLOFEC = datiForm.getData("GARESTATI.DPLETTCOMESCLOFEC");
      dataColumnContainerGARESTATI.setValue("GARESTATI.DPLETTCOMESCL", DPLETTCOMESCLOFEC);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.DPLETTCOMESCL").setObjectOriginalValue(datamtp);
      dataColumnContainerGARESTATI.getColumn("GARESTATI.DPLETTCOMESCL").setChiave(false);


      if(this.geneManager.countOccorrenze("GARESTATI", "CODGAR=? and NGARA=? and FASGAR=?",
              new Object[]{codiceTornata, numeroGara, faseGaraEc}) > 0)
          gestoreGARESTATI.update(status, dataColumnContainerGARESTATI);
      else
          gestoreGARESTATI.inserisci(status, dataColumnContainerGARESTATI);

    }
  }


  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}