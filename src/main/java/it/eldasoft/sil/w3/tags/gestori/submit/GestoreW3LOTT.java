/*
  * Created on 20/ott/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.gestori.submit;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreW3LOTT extends AbstractGestoreChiaveNumerica {

  static Logger logger = Logger.getLogger(GestoreW3LOTT.class);

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "NUMGARA" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "NUMLOTT";
  }

  @Override
  public String getEntita() {
    return "W3LOTT";
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
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    // Memorizzazione UUID
    UUID uuid = UUID.randomUUID();
    datiForm.addColumn("W3LOTT.LOTTO_UUID", uuid.toString());

    super.preInsert(status, datiForm);
    this.gestioneUlterioriCategorie(status, datiForm);
    this.gestioneCondizioni(status, datiForm);
    this.gestioneCPVSecondarie(status, datiForm);
    this.gestioneCup(status, datiForm);
    this.gestioneMisurePremiali(status, datiForm);
    this.gestioneMotiviDeroga(status,datiForm);

    // Gestione dei campi multipli per la Tipologia Lavoro
    this.gestisciTipologiaLavoro(status, datiForm);

    // Gestione dei campi multipli per la Tipologia Fornitura
    this.gestisciTipologiaFornitura(status, datiForm);

    // Importo della gara
    if (datiForm.isColumn("W3LOTT.NUMGARA")) {
    	try {
    		Long numgara = datiForm.getLong("W3LOTT.NUMGARA");
	        Object importoObj = sqlManager.getObject("select SUM(" + sqlManager.getDBFunction("isnull", new String[] {"IMPORTO_LOTTO", "0.00" }) + ") from W3LOTT where NUMGARA=? and STATO_SIMOG in (1,2,3,4,7,99)", 
	            new Object[] { numgara });

	        Double importo = new Double(0);
	        if (importoObj != null) {
	              if (!(importoObj instanceof Double)) {
	                importo = new Double(importoObj.toString());
	              } else {
	                importo = (Double) importoObj;
	              }
	            } 
	    	Long numero_lotti = (Long) this.sqlManager.getObject("select count(*) from w3lott where numgara = ? and stato_simog in (1,2,3,4,7,99)", new Object[] { numgara });
	    	if (datiForm.isColumn("W3LOTT.IMPORTO_LOTTO")) {
	    		importo += datiForm.getDouble("W3LOTT.IMPORTO_LOTTO");
	    	}
	    	String updateW3GARA = "update w3gara set importo_gara = ?, numero_lotti = ? where numgara = ?";
	    	this.sqlManager.update(updateW3GARA, new Object[] { importo, numero_lotti + 1, numgara });

    	} catch(Exception ex) {
    		;
    	}
    }
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreW3LOTT: inizio metodo");

    Long numgara = datiForm.getLong("W3LOTT.NUMGARA");
    Long numlott = datiForm.getLong("W3LOTT.NUMLOTT");

    this.getGeneManager().deleteTabelle(new String[] { "W3LOTTTIPI", "W3LOTTCUP", "W3LOTTCATE", "W3COND", "W3CPV", "W3MISPREMIALI","W3LOTTDEROGHE" },
        "NUMGARA = ? AND NUMLOTT = ?", new Object[] { numgara, numlott });

    if (logger.isDebugEnabled()) logger.debug("GestoreW3LOTT: fine metodo");

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    boolean isModifiedTipiAppalto = false;
    DataColumn[] datiTipiAppalto = datiForm.getColumns("W3LOTTTIPI",0);
    for (int i = 0; i < datiTipiAppalto.length; i++) {
      //DataColumn a = datiTipiAppalto[i];
      //JdbcParametro b1 = a.getOriginalValue();
      //JdbcParametro b2 = a.getValue();
      //boolean b3 = a.isModified();
      if(datiTipiAppalto[i].isModified()){
        isModifiedTipiAppalto = true;
        break;
      }
    }

    if (datiForm.isModifiedTable("W3LOTT") || datiForm.isModifiedTable("W3LOTTCUP") || isModifiedTipiAppalto || datiForm.isModifiedTable("W3LOTTCATE") || datiForm.isModifiedTable("W3COND") || datiForm.isModifiedTable("W3CPV") 
        || datiForm.isModifiedTable("W3MISPREMIALI") || datiForm.isModifiedTable("W3LOTTDEROGHE")) {
      String cig = datiForm.getString("W3LOTT.CIG");
      if (cig != null && cig.trim().length() > 0) {
        datiForm.setValue("W3LOTT.STATO_SIMOG", new Long(3));
      }
    }

    this.gestioneUlterioriCategorie(status, datiForm);
    this.gestioneCPVSecondarie(status, datiForm);
    this.gestioneCondizioni(status, datiForm);
    this.gestioneCup(status, datiForm);
    this.gestioneMisurePremiali(status, datiForm);
    this.gestioneMotiviDeroga(status,datiForm);

    GeneManager geneManager = this.getGeneManager();

    // Cancellazione preliminare delle tabelle figlie
    Long numgara = (Long) datiForm.getColumn("W3LOTT.NUMGARA").getValue().getValue();
    Long numlott = (Long) datiForm.getColumn("W3LOTT.NUMLOTT").getValue().getValue();
    geneManager.deleteTabelle(new String[] { "W3LOTTTIPI"}, "NUMGARA = ? AND NUMLOTT = ? ", new Object[] { numgara, numlott });
    //geneManager.deleteTabelle(new String[] { "W3COND"}, "NUMGARA = ? AND NUMLOTT = ? ", new Object[] { numgara, numlott });
    
    // Gestione dei campi multipli per la Tipologia Lavoro
    this.gestisciTipologiaLavoro(status, datiForm);

    // Gestione dei campi multipli per la Tipologia Fornitura
    this.gestisciTipologiaFornitura(status, datiForm);

    // Importo della gara
   	try {
        Object importoObj = sqlManager.getObject("select SUM(" + sqlManager.getDBFunction("isnull", new String[] {"IMPORTO_LOTTO", "0.00" }) + ") from W3LOTT where NUMGARA=? and STATO_SIMOG in (1,2,3,4,7,99)", 
        new Object[] { numgara });

        Double importo = new Double(0);
        if (importoObj != null) {
        	if (!(importoObj instanceof Double)) {
        		importo = new Double(importoObj.toString());
            } else {
                importo = (Double) importoObj;
	        }
	    } 
	    Long numero_lotti = (Long) this.sqlManager.getObject("select count(*) from w3lott where numgara = ? and stato_simog in (1,2,3,4,7,99)", new Object[] { numgara });
	    if (datiForm.isColumn("W3LOTT.IMPORTO_LOTTO")) {
	    	Double importoLotto = (Double)datiForm.getColumn("W3LOTT.IMPORTO_LOTTO").getOriginalValue().getValue();
    		importo = importo -importoLotto + datiForm.getDouble("W3LOTT.IMPORTO_LOTTO");
    	}
	    String updateW3GARA = "update w3gara set importo_gara = ?, numero_lotti = ? where numgara = ?";
	    this.sqlManager.update(updateW3GARA, new Object[] { importo, numero_lotti, numgara });

   	} catch(Exception ex) {
    	;
    }
  }

  /**
   *
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void gestioneUlterioriCategorie(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneUlterioriCategorie: inizio metodo");

    AbstractGestoreChiaveNumerica gestoremultiploW3LOTTCATE = new DefaultGestoreEntitaChiaveNumerica(
        "W3LOTTCATE", "NUMCATE", new String[] { "NUMGARA", "NUMLOTT" },
        this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoremultiploW3LOTTCATE, "W3LOTTCATE", new DataColumn[] {
            datiForm.getColumn("W3LOTT.NUMGARA"),
            datiForm.getColumn("W3LOTT.NUMLOTT") }, null);

    if (logger.isDebugEnabled())
      logger.debug("gestioneUlterioriCategorie: fine metodo");

  }

  /**
  *
  * @param status
  * @param datiForm
  * @throws GestoreException
  */
 private void gestioneCondizioni(TransactionStatus status,
     DataColumnContainer datiForm) throws GestoreException {

   if (logger.isDebugEnabled())
     logger.debug("gestioneCondizioni: inizio metodo");

   AbstractGestoreChiaveNumerica gestoremultiploW3COND = new DefaultGestoreEntitaChiaveNumerica(
       "W3COND", "NUM_COND", new String[] { "NUMGARA", "NUMLOTT" },
       this.getRequest());
   this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
		   gestoremultiploW3COND, "W3COND", new DataColumn[] {
           datiForm.getColumn("W3LOTT.NUMGARA"),
           datiForm.getColumn("W3LOTT.NUMLOTT") }, null);

   if (logger.isDebugEnabled())
     logger.debug("gestioneCondizioni: fine metodo");

 }
  
 /**
 *
 * @param status
 * @param datiForm
 * @throws GestoreException
 */
private void gestioneCPVSecondarie(TransactionStatus status,
    DataColumnContainer datiForm) throws GestoreException {

  if (logger.isDebugEnabled())
    logger.debug("gestioneCPVSecondarie: inizio metodo");

  AbstractGestoreChiaveNumerica gestoremultiploW3CPV = new DefaultGestoreEntitaChiaveNumerica(
      "W3CPV", "NUM_CPV", new String[] { "NUMGARA", "NUMLOTT" },
      this.getRequest());
  this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
		  gestoremultiploW3CPV, "W3CPV", new DataColumn[] {
          datiForm.getColumn("W3LOTT.NUMGARA"),
          datiForm.getColumn("W3LOTT.NUMLOTT") }, null);

  if (logger.isDebugEnabled())
    logger.debug("gestioneCPVSecondarie: fine metodo");

}

  /**
  *
  * @param status
  * @param datiForm
  * @throws GestoreException
  */
 private void gestioneCup(TransactionStatus status,
     DataColumnContainer datiForm) throws GestoreException {

   if (logger.isDebugEnabled())
     logger.debug("gestioneCup: inizio metodo");

   AbstractGestoreChiaveNumerica gestoremultiploW3LOTTCUP = new DefaultGestoreEntitaChiaveNumerica(
       "W3LOTTCUP", "NUMCUP", new String[] { "NUMGARA", "NUMLOTT" },
       this.getRequest());
   this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
       gestoremultiploW3LOTTCUP, "W3LOTTCUP", new DataColumn[] {
           datiForm.getColumn("W3LOTT.NUMGARA"),
           datiForm.getColumn("W3LOTT.NUMLOTT") }, null);

   if (logger.isDebugEnabled())
     logger.debug("gestioneCup: fine metodo");

 }
 
 private void gestioneMisurePremiali(TransactionStatus status,
     DataColumnContainer datiForm) throws GestoreException {

   if (logger.isDebugEnabled())
     logger.debug("gestioneMisurePremiali: inizio metodo");

   AbstractGestoreChiaveNumerica gestoremultiploW3MISPREMIALI = new DefaultGestoreEntitaChiaveNumerica(
       "W3MISPREMIALI", "IDMISURA", new String[] { "NUMGARA", "NUMLOTT" },
       this.getRequest());
   this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
       gestoremultiploW3MISPREMIALI, "W3MISPREMIALI", new DataColumn[] {
           datiForm.getColumn("W3LOTT.NUMGARA"),
           datiForm.getColumn("W3LOTT.NUMLOTT") }, null);

   if (logger.isDebugEnabled())
     logger.debug("gestioneMisurePremiali: fine metodo");

 }
 private void gestioneMotiviDeroga(TransactionStatus status,
     DataColumnContainer datiForm) throws GestoreException {

   if (logger.isDebugEnabled())
     logger.debug("gestioneMotiviDeroga: inizio metodo");

   AbstractGestoreChiaveNumerica gestoremultiploW3LOTTDEROGHE = new DefaultGestoreEntitaChiaveNumerica(
       "W3LOTTDEROGHE", "IDDEROGA", new String[] { "NUMGARA", "NUMLOTT" },
       this.getRequest());
   this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
       gestoremultiploW3LOTTDEROGHE, "W3LOTTDEROGHE", new DataColumn[] {
           datiForm.getColumn("W3LOTT.NUMGARA"),
           datiForm.getColumn("W3LOTT.NUMLOTT") }, null);

   if (logger.isDebugEnabled())
     logger.debug("gestioneMotiviDeroga: fine metodo");

 }


  /**
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void gestisciTipologiaLavoro(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException {

    if (datiForm.isColumn("TIPOLOGIALAVORO6")) {
      String tipologiaLavoro6 = (String) datiForm.getColumn("TIPOLOGIALAVORO6").getValue().getValue();
      if ("1".equals(tipologiaLavoro6))
        this.inserisciW3LOTTTIPI(status, datiForm, 6);
    }

    if (datiForm.isColumn("TIPOLOGIALAVORO7")) {
      String tipologiaLavoro7 = (String) datiForm.getColumn("TIPOLOGIALAVORO7").getValue().getValue();
      if ("1".equals(tipologiaLavoro7))
        this.inserisciW3LOTTTIPI(status, datiForm, 7);
    }

    if (datiForm.isColumn("TIPOLOGIALAVORO8")) {
      String tipologiaLavoro8 = (String) datiForm.getColumn("TIPOLOGIALAVORO8").getValue().getValue();
      if ("1".equals(tipologiaLavoro8))
        this.inserisciW3LOTTTIPI(status, datiForm, 8);
    }

    if (datiForm.isColumn("TIPOLOGIALAVORO9")) {
      String tipologiaLavoro9 = (String) datiForm.getColumn("TIPOLOGIALAVORO9").getValue().getValue();
      if ("1".equals(tipologiaLavoro9))
        this.inserisciW3LOTTTIPI(status, datiForm, 9);
    }

    if (datiForm.isColumn("TIPOLOGIALAVORO10")) {
      String tipologiaLavoro10 = (String) datiForm.getColumn(
          "TIPOLOGIALAVORO10").getValue().getValue();
      if ("1".equals(tipologiaLavoro10))
        this.inserisciW3LOTTTIPI(status, datiForm, 10);
    }

    if (datiForm.isColumn("TIPOLOGIALAVORO11")) {
      String tipologiaLavoro11 = (String) datiForm.getColumn(
          "TIPOLOGIALAVORO11").getValue().getValue();
      if ("1".equals(tipologiaLavoro11))
        this.inserisciW3LOTTTIPI(status, datiForm, 11);
    }

    if (datiForm.isColumn("TIPOLOGIALAVORO12")) {
      String tipologiaLavoro12 = (String) datiForm.getColumn(
          "TIPOLOGIALAVORO12").getValue().getValue();
      if ("1".equals(tipologiaLavoro12))
        this.inserisciW3LOTTTIPI(status, datiForm, 12);
    }
    
    if (datiForm.isColumn("TIPOLOGIALAVORO13")) {
        String tipologiaLavoro13 = (String) datiForm.getColumn(
            "TIPOLOGIALAVORO13").getValue().getValue();
        if ("1".equals(tipologiaLavoro13))
          this.inserisciW3LOTTTIPI(status, datiForm, 13);
      }
  }

  /**
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void gestisciTipologiaFornitura(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException {

    if (datiForm.isColumn("TIPOLOGIAFORNITURA1")) {
      String tipologiaLavoro1 = (String) datiForm.getColumn("TIPOLOGIAFORNITURA1").getValue().getValue();
      if ("1".equals(tipologiaLavoro1))
        this.inserisciW3LOTTTIPI(status, datiForm, 1);
    }

    if (datiForm.isColumn("TIPOLOGIAFORNITURA2")) {
      String tipologiaLavoro2 = (String) datiForm.getColumn("TIPOLOGIAFORNITURA2").getValue().getValue();
      if ("1".equals(tipologiaLavoro2))
        this.inserisciW3LOTTTIPI(status, datiForm, 2);
    }

    if (datiForm.isColumn("TIPOLOGIAFORNITURA3")) {
      String tipologiaLavoro3 = (String) datiForm.getColumn("TIPOLOGIAFORNITURA3").getValue().getValue();
      if ("1".equals(tipologiaLavoro3))
        this.inserisciW3LOTTTIPI(status, datiForm, 3);
    }

    if (datiForm.isColumn("TIPOLOGIAFORNITURA4")) {
      String tipologiaLavoro4 = (String) datiForm.getColumn("TIPOLOGIAFORNITURA4").getValue().getValue();
      if ("1".equals(tipologiaLavoro4))
        this.inserisciW3LOTTTIPI(status, datiForm, 4);
    }

    if (datiForm.isColumn("TIPOLOGIAFORNITURA5")) {
      String tipologiaLavoro5 = (String) datiForm.getColumn("TIPOLOGIAFORNITURA5").getValue().getValue();
      if ("1".equals(tipologiaLavoro5))
        this.inserisciW3LOTTTIPI(status, datiForm, 5);
    }

  }

  /**
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void inserisciW3LOTTTIPI(TransactionStatus status,
      DataColumnContainer datiForm, long idappalto) throws GestoreException {

    DataColumnContainer datiFormW3LOTTTIPI = new DataColumnContainer("W3LOTTTIPI.NUMTIPI");
    datiFormW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMGARA", datiForm.getColumn("W3LOTT.NUMGARA"));
    datiFormW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMLOTT", datiForm.getColumn("W3LOTT.NUMLOTT"));
    datiFormW3LOTTTIPI.addColumn("W3LOTTTIPI.IDAPPALTO", JdbcParametro.TIPO_NUMERICO, new Long(idappalto));
    this.inserisci(status, datiFormW3LOTTTIPI, new GestoreW3LOTTTIPI());
  }

}
