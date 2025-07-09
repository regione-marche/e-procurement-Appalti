/*
 * Created on 22-05-2012
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GestoreProfili;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per salvataggio dati generali degli avvisi
 *
 * @author Marcello Caminiti
 */
public class GestoreAvvisi extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreAvvisi.class);

  /** Manager di PG */
  private PgManager        pgManager        = null;

  /** Manager per l'esecuzione di query */
  private SqlManager       sqlManager       = null;

  private PgManagerEst1        pgManagerEst1        = null;

  @Override
  public String getEntita() {
    return "GAREAVVISI";
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

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);

  }

	public GestoreAvvisi() {
		super();
	}

	/**
	 * @param isGestoreStandard
	 */
	public GestoreAvvisi(boolean isGestoreStandard) {
		super(isGestoreStandard);
	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  //variabili per tracciatura eventi
	  int livEvento = 1;
	  String codEvento = "GA_ELIMINAZIONE_PROCEDURA";
	  String oggEvento = datiForm.getString("GAREAVVISI.NGARA");
	  String descrEvento = "Eliminazione dell'avviso";
	  String errMsgEvento = "";

	  // Chiamo la funzione centralizzata per l'eliminazione della gara per albo
      // fornitori, la quale effettua la cancellazione di tutte le entita' figlie
      String codiceGara = "$" + datiForm.getString("GAREAVVISI.NGARA");
      pgManager.deleteTORN(codiceGara);
      try {
        sqlManager.update("delete from torn where codgar=?", new Object[]{codiceGara});
      } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
        throw new GestoreException("Errore nella cancellazione dell'occorenza da TORN",
            null, e);
      }finally{
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
        }

      }
	}

	@Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  String regex = "^[a-zA-Z0-9]+$";

	  if(datiForm.isColumn("GARE.CUPPRG") && datiForm.isModifiedColumn("GARE.CUPPRG") && datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()!=null
          && !"".equals(datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()) && (datiForm.getColumn("GARE.CUPPRG").getValue().stringValue().length()!= 15 || !datiForm.getColumn("GARE.CUPPRG").getValue().stringValue().matches(regex)))
        throw new GestoreException("Il codice CUP specificato non è valido","controlloCodiceCUP");

	//variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_CREAZIONE_PROCEDURA";
    String oggEvento = "";
    String descrEvento = "Inserimento dell'avviso";
    String errMsgEvento = "";

	String nuovoNGARA = null;
	String nuovoCODGAR = null;
	try{
      if(geneManager.isCodificaAutomatica("GAREAVVISI", "NGARA")){
        nuovoNGARA =  geneManager.calcolaCodificaAutomatica("GAREAVVISI","NGARA");
        nuovoCODGAR = "$".concat(nuovoNGARA);
      } else {
        String numeroGaraDestinazione = datiForm.getString("GAREAVVISI.NGARA");
        numeroGaraDestinazione = numeroGaraDestinazione.trim();
        pgManager.verificaPreliminareDatiCopiaGara(null, numeroGaraDestinazione,
            null, numeroGaraDestinazione, false);
        nuovoNGARA = numeroGaraDestinazione;
        nuovoCODGAR = "$".concat(numeroGaraDestinazione);
      }
      oggEvento = nuovoNGARA;

	  // Salvataggio delle informazioni minime nella tabella TORN (come
      // una vera e propria gara a lotto unico)
      datiForm.getColumn("GARE.CODGAR1").setValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, nuovoCODGAR));
      datiForm.getColumn("GARE.NGARA").setValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, nuovoNGARA));
      datiForm.getColumn("GARE.CODGAR1").setChiave(true);
      datiForm.getColumn("GARE.NGARA").setChiave(true);

      // Inizializzazione del campo GENERE
      datiForm.addColumn("GARE.GENERE", new Long(11));
      try {
        datiForm.insert("GARE", sqlManager);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'insert dell'occorrenza in GARE",null, e);
      }

      try {
			String verNPA = pgManager.getVersioneNpaCorrente();
			if (StringUtils.isNotBlank(verNPA)) {
				if (!datiForm.isColumn("TORN.VERNPA")) {
					datiForm.addColumn("TORN.VERNPA", verNPA);
				} else {
					datiForm.setValue("TORN.VERNPA", verNPA);
				}
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore nella lettura dei dati relativi a NPA", null, e);
		}

      GestoreProfili gestoreProfili = this.getGeneManager().getProfili();
      if (StringUtils.isNotBlank(ConfigManager.getValore("comunicazioni.notifiche.idcfg"))
    		  && gestoreProfili.checkProtec((String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS","GARE.TORN.INDNOTIFICHE")
    		  && gestoreProfili.checkProtec((String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "SEZ", "VIS","GARE.GAREAVVISI-scheda.DATIGEN.NOTIFICHECOMUNICAZIONI")) {
			String indirizziDefault = ConfigManager.getValore("comunicazioni.notifiche.indirizziDefault");
			ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
			if (StringUtils.isBlank(indirizziDefault)) {
				indirizziDefault = profilo.getMail();
			}
			if (StringUtils.isNotBlank(indirizziDefault)) {
				if (!datiForm.isColumn("TORN.INDNOTIFICHE")) {
					datiForm.addColumn("TORN.INDNOTIFICHE", indirizziDefault);
				}
				datiForm.setValue("TORN.INDNOTIFICHE", indirizziDefault);
			}
		}

      // Salvataggio delle informazioni minime nella tabella TORN (come
      // una vera e propria gara a lotto unico)
      datiForm.addColumn("TORN.CODGAR", JdbcParametro.TIPO_TESTO, nuovoCODGAR);
      datiForm.getColumn("TORN.CODGAR").setChiave(true);
      datiForm.getColumn("TORN.CENINT").setOriginalValue(null);
      // Il campo TORN.TIPGEN non e' necessario inizializzarlo visto che non
      // viene usato nelle fasi di ricezione offerte
      try {
        datiForm.insert("TORN", sqlManager);
      } catch (SQLException e) {
            throw new GestoreException("Errore nell'insert dell'occorrenza in TORN",
                    null, e);
      }

      datiForm.getColumn("GAREAVVISI.NGARA").setChiave(true);
      datiForm.setValue("GAREAVVISI.NGARA", nuovoNGARA);
      datiForm.getColumn("GAREAVVISI.CODGAR").setChiave(true);
      datiForm.setValue("GAREAVVISI.CODGAR", nuovoCODGAR);

      // INSERIMENTO PERMESSI DI ACCESSO ALLA GARA
      this.inserisciPermessi(datiForm, "CODGAR", new Integer(2));

      //Gestione entita GARALTSOG
      pgManagerEst1.gestioneGaraltSog(datiForm);

	}catch (GestoreException e){
	  livEvento = 3;
	  errMsgEvento = e.getMessage();
	}finally{
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
      }
	}
    //Aggiornamenti per la pagina "Altri Dati"
    if (datiForm.isColumn("GARCPV.CODCPV")) {
        //Gestione CPV
        this.updateGARCPV(status, datiForm, nuovoNGARA);
    }
    
    //Aggiornamenti CUP
    AbstractGestoreChiaveIDAutoincrementante gestoreGARECUP = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "GARECUP", "ID", this.getRequest());
    pgManagerEst1.checkAndInsertCUP(status,datiForm,gestoreGARECUP,"GARECUP",nuovoNGARA, this.getRequest());

  }

	@Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  String regex = "^[a-zA-Z0-9]+$";

      if(datiForm.isColumn("GARE.CUPPRG") && datiForm.isModifiedColumn("GARE.CUPPRG") && datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()!=null
          && !"".equals(datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()) && (datiForm.getColumn("GARE.CUPPRG").getValue().stringValue().length()!= 15 || !datiForm.getColumn("GARE.CUPPRG").getValue().stringValue().matches(regex)))
        throw new GestoreException("Il codice CUP specificato non è valido","controlloCodiceCUP");

        try {
            if (datiForm.isColumn("GARE.CUPPRG") && datiForm.isModifiedColumn("GARE.CUPPRG")) {
                pgManagerEst1.inserisciSchedaID(datiForm.getColumn("GAREAVVISI.NGARA").getValue().getStringValue(), this.getRequest(), status);
            }
        } catch (Throwable e) {
            throw new GestoreException("Errore nell'inserimento della scheda ID", null, e);
        }

	  try {
        datiForm.addColumn("TORN.CODGAR", JdbcParametro.TIPO_TESTO);
        datiForm.update("TORN", sqlManager);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'update dell'occorrenza in TORN",
                null, e);
      }


      try {
        datiForm.addColumn("GARE.NGARA", JdbcParametro.TIPO_TESTO);
        datiForm.update("GARE", sqlManager);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'update dell'occorrenza in GARE",
                null, e);
      } 


      // Gestione delle sezioni pubblicazioni
      AbstractGestoreChiaveNumerica gestorePUBBLI = new DefaultGestoreEntitaChiaveNumerica(
          "PUBBLI", "NUMPUB", new String[] {"CODGAR9"}, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestorePUBBLI, "PUBBANDO",
          new DataColumn[] {datiForm.getColumn("GAREAVVISI.CODGAR") }, null);

      datiForm.getColumn("GAREAVVISI.NGARA").setChiave(true);
      datiForm.getColumn("GAREAVVISI.CODGAR").setChiave(true);

      //Gestione entita GARALTSOG
      //nel caso in cui si modifichi il campo TORN.ALTRISOG e lo si imposti ad un valore <>2 o nel caso si cambi TORN.CENINT
      //si devono cancellare la occorrenze da GARALTOG
      if(datiForm.isColumn("TORN.CENINT") && datiForm.isColumn("TORN.ALTRISOG") && (datiForm.isModifiedColumn("TORN.CENINT") ||
          (datiForm.isModifiedColumn("TORN.ALTRISOG") && !(new Long(2).equals(datiForm.getLong("TORN.ALTRISOG")))))) try {
        this.sqlManager.update("delete from garaltsog where ngara=?", new Object[]{datiForm.getString("GARE.NGARA")});
      } catch (SQLException e) {
        throw new GestoreException("Errore nella ancellazione dei dati in GARALTSOG", null, e);
      }
      pgManagerEst1.gestioneGaraltSog(datiForm);


      //Aggiornamenti per la pagina "Altri Dati"
      if (datiForm.isColumn("GARCPV.CODCPV")) {
          //Gestione CPV
          this.updateGARCPV(status, datiForm, datiForm.getColumn("GAREAVVISI.NGARA").getValue().getStringValue());
      }
      
      //Aggiornamenti CUP
      AbstractGestoreChiaveIDAutoincrementante gestoreGARECUP = new DefaultGestoreEntitaChiaveIDAutoincrementante(
          "GARECUP", "ID", this.getRequest());
      pgManagerEst1.checkAndInsertCUP(status,datiForm,gestoreGARECUP,"GARECUP",datiForm.getString("GARE.NGARA"), this.getRequest());

      String bloccoModificaPCP = UtilityStruts.getParametroString(this.getRequest(), "bloccoModificaPCP");
      String descrErrMsg = "";
      if("true".equalsIgnoreCase(bloccoModificaPCP)){
    	  HashMap<String, DataColumn> hm = datiForm.getColonne();
          Iterator<?> it = hm.entrySet().iterator();

          boolean tracciareCampo=true;
          boolean modificheDaTracciare=false;
    	  while (it.hasNext()) {
              Entry<String, DataColumn> pair = (Entry<String, DataColumn>)it.next();
              if(pair.getValue().isModified()){
                //condizioni per non tracciare i campi:
                //campi fittizZi che contengo nel nome la stringa "FIT"
                //Campi fittizzi delle sezioni dinamiche che contengono nel nome le stringhe "INDICE_" e "MOD_"
                //Campi che contengono nei nomi le stringhe: "NGARA","TIPCPV","TIPLAVG","IMPAPP","CIG","NUMCPV"
                if(pair.getKey().indexOf("FIT")>=0 || pair.getKey().indexOf("INDICE_")>=0 || pair.getKey().indexOf("MOD_")>=0  ||
                    pair.getKey().indexOf("NGARA")>=0 || pair.getKey().indexOf("TIPCPV")>=0 || pair.getKey().indexOf("TIPLAVG")>=0 ||
                    (pair.getKey().indexOf("CIG")>=0 && !"GARE1.MOTESENTECIG".equals(pair.getKey())) ||
                    pair.getKey().indexOf("NUMCPV")>=0 || pair.getKey().indexOf("STATOGARA")>=0)
                  tracciareCampo=false;
                else
                  tracciareCampo=true;

                if(tracciareCampo){
                  modificheDaTracciare = true;
                  if(!"".equals(descrErrMsg)){
                    descrErrMsg+="\n";
                  }
                  if(pair.getKey().indexOf("DEL_CPVCOMP")>0){
                    //Nel caso di eliminazione di CPV complementari non ho le chiavi della occorrenza eliminata, quindi
                    //il messaggio sarà generico
                    descrErrMsg+= "Eliminata una occorrenza di CPVCOMP.";
                  }else{
                    JdbcParametro paramOrigin = pair.getValue().getOriginalValue();
                    String valOriginale = "";
                    if(paramOrigin!=null){
                      valOriginale = paramOrigin.toString();
                    }
                    JdbcParametro paramNuovo = pair.getValue().getValue();
                    String valNuovo = "";
                    if(paramNuovo!=null){
                      valNuovo = paramNuovo.toString();
                    }
                    if("".equals(valOriginale)){valOriginale = "NULL";}
                    if("".equals(valNuovo)){valNuovo = "NULL";}
                    descrErrMsg+= pair.getKey() + ": ";
                    descrErrMsg+= "valore vecchio= " + valOriginale + ", ";
                    descrErrMsg+= "valore nuovo= " + valNuovo;
                  }
                }
              }
          }
    	  if(modificheDaTracciare){
	          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
	          logEvento.setLivEvento(1);
	          logEvento.setOggEvento(datiForm.getColumn("GAREAVVISI.NGARA").getValue().stringValue());
	          logEvento.setCodEvento("GA_MODIFICA_PROCEDURA_PUB");
	          String descrizioneEvento = "Modifica dati avviso dopo invio schede ad ANAC.";
	          logEvento.setDescr(descrizioneEvento);
	          logEvento.setErrmsg("Dettaglio modifiche:\n" + descrErrMsg);
	          LogEventiUtils.insertLogEventi(logEvento);
    	  }
      }
	}
	  /**
	   * Aggiorna sia la prima occorrenza dei CPV, sia le n occorrenze successive
	   *
	   * @param status
	   * @param datiForm
	   * @throws GestoreException
	   */
	  private void updateGARCPV(TransactionStatus status,
	      DataColumnContainer datiForm, String nuovoNGARA) throws GestoreException {
	    // Aggiornamento dell'occorrenza principale
	    if (datiForm.isModifiedColumn("GARCPV.CODCPV")) {
	      Vector<DataColumn> colonneGARCPV = new Vector<DataColumn>();
	      datiForm.getColumn("GARCPV.NGARA").setValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, nuovoNGARA));
	      colonneGARCPV.add(datiForm.getColumn("GARCPV.NGARA"));
	      colonneGARCPV.add(datiForm.getColumn("GARCPV.NUMCPV"));
	      colonneGARCPV.add(datiForm.getColumn("GARCPV.CODCPV"));
	      colonneGARCPV.add(datiForm.getColumn("GARCPV.TIPCPV"));

	      DefaultGestoreEntitaChiaveNumerica gestoreGARCPV = new DefaultGestoreEntitaChiaveNumerica(
	          "GARCPV", "NUMCPV", new String[] { "NGARA" }, this.getRequest());
	      if (datiForm.getColumn("GARCPV.NUMCPV").getValue().getValue() == null)
	        gestoreGARCPV.inserisci(status, new DataColumnContainer(colonneGARCPV));
	      else
	        gestoreGARCPV.update(status, new DataColumnContainer(colonneGARCPV));
	    }

	    // Aggiornamento delle occorrenze complementari
	    AbstractGestoreChiaveNumerica gestoreGARCPV = new DefaultGestoreEntitaChiaveNumerica(
	        "GARCPV", "NUMCPV", new String[] { "NGARA" }, this.getRequest());
	    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
	        gestoreGARCPV, "CPVCOMP",
	        new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);

	  }

	@Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

}