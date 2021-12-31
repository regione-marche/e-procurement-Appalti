/*
 * Created on 26-lug-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per salvataggio dati generali di una gara per Elenco operatori economici
 *
 * @author Luca.Giacomazzo
 */
public class GestoreElencoDitte extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreGARE.class);

  /** Manager di PG */
  private PgManager        pgManager        = null;

  /** Manager per l'esecuzione di query */
  private SqlManager       sqlManager       = null;

  /** Manager per l'estrazione di dati tabellati */
  //private TabellatiManager tabellatiManager = null;

  @Override
  public String getEntita() {
    return "TORN";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager di Piattaforma Gare
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    // Estraggo il manager per eseguire query sui tabellati
    //tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
    //    "tabellatiManager", this.getServletContext(), TabellatiManager.class);
  }

	public GestoreElencoDitte() {
		super();
	}

	/**
	 * @param isGestoreStandard
	 */
	public GestoreElencoDitte(boolean isGestoreStandard) {
		super(isGestoreStandard);
	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	//variabili per tracciatura eventi
	int livEvento = 1;
	String codEvento = "GA_ELIMINAZIONE_PROCEDURA";
	String oggEvento = "";
	String descrEvento = null;
	String errMsgEvento = "";
	String messageKey = "errors.logEventi.inaspettataException";

	String genere = UtilityStruts.getParametroString(this.getRequest(),
        "genere");

    if("20".equals(genere))
      descrEvento = "Eliminazione del catalogo";
    else
      descrEvento = "Eliminazione dell'elenco";

	try{
	  // Chiamo la funzione centralizzata per l'eliminazione della gara per albo
		// fornitori, la quale effettua la cancellazione di tutte le entita' figlie
		String codiceGara = datiForm.getString("TORN.CODGAR");
		if(codiceGara.indexOf("$") >= 0)
			datiForm.addColumn("GARE.NGARA", JdbcParametro.TIPO_TESTO, codiceGara.substring(1));
		else
			datiForm.addColumn("GARE.NGARA", JdbcParametro.TIPO_TESTO, codiceGara);
		datiForm.getColumn("GARE.NGARA").setChiave(true);
		oggEvento = datiForm.getString("GARE.NGARA");
		try{
		  pgManager.deleteTORN(codiceGara);
		}catch(GestoreException e){
		  livEvento = 3;
		  errMsgEvento=e.getMessage();
		}
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
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	//variabili per tracciatura eventi
	int livEvento = 1;
	String codEvento = "GA_CREAZIONE_PROCEDURA";
	String oggEvento = "";
	String descrEvento = "";
	String errMsgEvento = "";
	String messageKey = "errors.logEventi.inaspettataException";

	try{
    	String nuovoNGARA = null;
    	String nuovoCODGAR = null;
    	//Long genere = new Long(10);

    	 String genere = UtilityStruts.getParametroString(this.getRequest(),
           "genere");

    	 if("20".equals(genere)){
    	   descrEvento = "Inserimento del catalogo";
    	   datiForm.setValue("GAREALBO.TIPOLOGIA",  new Long(1));
    	   if(geneManager.isCodificaAutomatica("MECATALOGO", "CODGAR")){
    	     HashMap hm = null;
    	     try{
        	     hm = pgManager.calcolaCodificaAutomatica("MECATALOGO", Boolean.TRUE,
                       null,   null);
    	     }catch(GestoreException e){
    	       livEvento = 3;
    	       errMsgEvento=e.getMessage();
    	       throw e;
    	     }
             nuovoNGARA =  (String) hm.get("numeroGara");
             nuovoCODGAR = (String) hm.get("codiceGara");

           } else {
             String numeroGaraDestinazione = datiForm.getString("GAREALBO.NGARA");
             try{
               pgManager.verificaPreliminareDatiCopiaGara(null, numeroGaraDestinazione,
                     null, numeroGaraDestinazione, false);
             }catch(GestoreException e){
               livEvento = 3;
               oggEvento = numeroGaraDestinazione;
               errMsgEvento=e.getMessage();
               throw e;
             }
             nuovoNGARA = numeroGaraDestinazione;
             nuovoCODGAR = "$".concat(numeroGaraDestinazione);
           }
    	   datiForm.getColumn("MECATALOGO.NGARA").setChiave(true);
    	    datiForm.setValue("MECATALOGO.NGARA", nuovoNGARA);
    	    datiForm.getColumn("MECATALOGO.CODGAR").setChiave(true);
    	    datiForm.setValue("MECATALOGO.CODGAR", nuovoCODGAR);
    	    //genere = new Long(20);
    	 }else{
    	   descrEvento = "Inserimento dell'elenco";
    	   if(geneManager.isCodificaAutomatica("GAREALBO", "CODGAR")){
    	     HashMap hm = null;
    	     try{
        	     hm = pgManager.calcolaCodificaAutomatica("GAREALBO", Boolean.TRUE,
        	            null,   null);
    	     }catch(GestoreException e){
    	       livEvento = 3;
    	       errMsgEvento=e.getMessage();
               throw e;
    	     }
    	      nuovoNGARA =  (String) hm.get("numeroGara");
    	      nuovoCODGAR = (String) hm.get("codiceGara");

    	    } else {
    	      String numeroGaraDestinazione = datiForm.getString("GAREALBO.NGARA");
    	      try{
        	      pgManager.verificaPreliminareDatiCopiaGara(null, numeroGaraDestinazione,
        	            null, numeroGaraDestinazione, false);
    	      }catch(GestoreException e){
                livEvento = 3;
                oggEvento = numeroGaraDestinazione;
                errMsgEvento=e.getMessage();
                throw e;
              }
    	      nuovoNGARA = numeroGaraDestinazione;
    	      nuovoCODGAR = "$".concat(numeroGaraDestinazione);
    	    }
    	 }

         oggEvento = nuovoNGARA;

        datiForm.getColumn("GAREALBO.NGARA").setChiave(true);
        datiForm.setValue("GAREALBO.NGARA", nuovoNGARA);
        datiForm.getColumn("GAREALBO.CODGAR").setChiave(true);
        datiForm.setValue("GAREALBO.CODGAR", nuovoCODGAR);

        Long tipologia = datiForm.getLong("GAREALBO.TIPOLOGIA");
        Long valiscr = null;
        Long gpreavrin = null;
        Long rifiscr = null;
        if(tipologia!=null && tipologia.longValue()!=3){
          valiscr = this.getGiorniDaTabellato("A1083", "1");
          datiForm.setValue("GAREALBO.VALISCR", valiscr);
          gpreavrin = this.getGiorniDaTabellato("A1083", "2");
          datiForm.setValue("GAREALBO.GPREAVRIN", gpreavrin);
          rifiscr = this.getGiorniDaTabellato("A1083", "3");
          datiForm.setValue("GAREALBO.RIFISCR", rifiscr);
          Long apprin = new Long(1);
          if(GeneManager.checkOP(this.getServletContext(), "OP114"))
            apprin = new Long(2);
          datiForm.setValue("GAREALBO.APPRIN", apprin);
        }

        // Salvataggio delle informazioni minime nella tabella TORN (come
    	// una vera e propria gara a lotto unico)

    		datiForm.getColumn("GARE.CODGAR1").setValue(new JdbcParametro(
    				JdbcParametro.TIPO_TESTO, nuovoCODGAR));
    		datiForm.getColumn("GARE.NGARA").setValue(new JdbcParametro(
    				JdbcParametro.TIPO_TESTO, nuovoNGARA));
    		datiForm.getColumn("GARE.CODGAR1").setChiave(true);
    		datiForm.getColumn("GARE.NGARA").setChiave(true);

    		// Inizializzazione del campo GENERE
    		datiForm.addColumn("GARE.GENERE", new Long(genere));
        try {
        	datiForm.insert("GARE", sqlManager);
    		} catch (SQLException e) {
    		  livEvento = 3;
              errMsgEvento = "Errore nell'insert dell'occorrenza in GARE";
              throw new GestoreException("Errore nell'insert dell'occorrenza in GARE",
    					null, e);
    		}

        try {
            //Inizializzazione del campo tipoalgo
            datiForm.addColumn("GAREALBO.TIPOALGO", JdbcParametro.TIPO_NUMERICO, new Long(6));
            //Inizializzazione del campo ISCRIRT
            datiForm.addColumn("GAREALBO.ISCRIRT", JdbcParametro.TIPO_TESTO, "2");
            datiForm.addColumn("GAREALBO.PUBOPE", JdbcParametro.TIPO_TESTO, "1");
            datiForm.addColumn("GAREALBO.TIPOCLASS", JdbcParametro.TIPO_NUMERICO, new Long(2));

           datiForm.insert("GAREALBO", sqlManager);
    		} catch (SQLException e) {
    		  livEvento = 3;
              errMsgEvento = "Errore nell'insert dell'occorrenza in GAREALBO";
    		  throw new GestoreException("Errore nell'insert dell'occorrenza in GAREALBO",
    					null, e);
    		}

    		 if("20".equals(genere)){
            	try {
            	   datiForm.insert("MECATALOGO", sqlManager);
            	} catch (SQLException e) {
            	  livEvento = 3;
                  errMsgEvento = "Errore nell'insert dell'occorrenza in MECATALOGO";
            	  throw new GestoreException("Errore nell'insert dell'occorrenza in MECATALOGO",null, e);
            	}
    		 }

        // Salvataggio delle informazioni minime nella tabella TORN (come
    		// una vera e propria gara a lotto unico)
        datiForm.addColumn("TORN.CODGAR", JdbcParametro.TIPO_TESTO, nuovoCODGAR);
        datiForm.getColumn("TORN.CODGAR").setChiave(true);
        // Il campo TORN.TIPGEN non e' necessario inizializzarlo visto che non
        // viene usato nelle fasi di ricezione offerte

        // INSERIMENTO PERMESSI DI ACCESSO ALLA GARA
        try{
          this.inserisciPermessi(datiForm, "CODGAR", new Integer(2));

          // Gestione delle sezioni pubblicazioni e termini di iscrizione
          AbstractGestoreChiaveNumerica gestorePUBBTERM = new DefaultGestoreEntitaChiaveNumerica(
              "PUBBTERM", "NUMPT", new String[] {"CODGAR", "NGARA" }, this.getRequest());
          this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
              gestorePUBBTERM, "PUBBLITERMISCR",
              new DataColumn[] {datiForm.getColumn("GARE.CODGAR1"),
          		datiForm.getColumn("GARE.NGARA") }, null);
        } catch (GestoreException e) {
          livEvento = 3;
          errMsgEvento=e.getMessage();;
        }
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
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  if(datiForm.isColumn("GAREALBO.TIPOLOGIA") && datiForm.isModifiedColumn("GAREALBO.TIPOLOGIA") ){
	  Long tipologia = datiForm.getLong("GAREALBO.TIPOLOGIA");
	    if(tipologia!=null && tipologia.longValue()==3){
	      datiForm.setValue("GAREALBO.VALISCR", null);
	      datiForm.setValue("GAREALBO.GPREAVRIN", null);
	      datiForm.setValue("GAREALBO.APPRIN", null);
	    }
	  }


	    if(datiForm.isColumn("GAREALBO.TIPOALGO") && datiForm.isModifiedColumn("GAREALBO.TIPOALGO")) {
      	    String ngara = datiForm.getString("GARE.NGARA");
      	    Long numGare = null;
      	    try {
                numGare = (Long)this.sqlManager.getObject("select count(ngara) from gare where elencoe = ? ", new Object[]{ngara});
              } catch (SQLException e) {
                throw new GestoreException("Errore nel determinare il numero di gare a cui è associato l'elenco",
                    null, e);
              }
              if(numGare!=null && numGare.longValue()>0){
                throw new GestoreException("Il criterio di rotazione non può essere modificato poiché vi sono delle gare associate all'elenco","noModificaCritRotaz");
              }
      	}

        try {
			datiForm.update("GAREALBO", sqlManager);
		} catch (SQLException e) {
			throw new GestoreException("Errore nell'update dell'occorrenza in GAREALBO",
					null, e);
		}

	    String genere = UtilityStruts.getParametroString(this.getRequest(),
         "genere");

	    if("20".equals(genere)){
  		try {
            datiForm.update("MECATALOGO", sqlManager);
          } catch (SQLException e) {
              throw new GestoreException("Errore nell'update dell'occorrenza in MECATALOGO",
                      null, e);
          }
	    }

		try {
			datiForm.addColumn("TORN.CODGAR", JdbcParametro.TIPO_TESTO);
			DataColumn campoTORN_CODGAR = datiForm.getColumn("TORN.CODGAR");
			campoTORN_CODGAR.setValue(datiForm.getColumn("GAREALBO.CODGAR").getValue());
			campoTORN_CODGAR.setOriginalValue(datiForm.getColumn("GAREALBO.CODGAR").getValue());
			campoTORN_CODGAR.setChiave(true);
			datiForm.update("TORN", sqlManager);
		} catch (SQLException e) {
			throw new GestoreException("Errore nell'update dell'occorrenza in TORN",
					null, e);
		}

    // Gestione delle sezioni pubblicazioni e termini di iscrizione
    AbstractGestoreChiaveNumerica gestorePUBBTERM = new DefaultGestoreEntitaChiaveNumerica(
        "PUBBTERM", "NUMPT", new String[] {"CODGAR", "NGARA" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestorePUBBTERM, "PUBBLITERMISCR",
        new DataColumn[] {datiForm.getColumn("GARE.CODGAR1"),
    		datiForm.getColumn("GARE.NGARA") }, null);
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

	private Long getGiorniDaTabellato(String tab1cod, String tab1tip){
	  Long giorni = null;
	  TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
	        this.getServletContext(), TabellatiManager.class);

	  String descrizione = tabellatiManager.getDescrTabellato(tab1cod, tab1tip);
	  if(descrizione!=null && !"".equals(descrizione)){
	    descrizione = descrizione.substring(0, descrizione.indexOf(" "));
	    giorni = new Long(descrizione);
	  }
	  return giorni;
	}

}