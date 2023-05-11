/*
 * Created on 1-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pl.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pl.struts.gestori.GestoreAPPR;
import it.eldasoft.sil.pl.struts.gestori.GestoreCONT;
import it.eldasoft.sil.pl.utils.UtilityConversioneCampi;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Classe che gestisce tutte le funzionalità di base di PL
 *
 * @author cit_franceschin, stefano.sabbadin
 */
public class PlManager {

  /** Logger */
  static Logger            logger = Logger.getLogger(PlManager.class);

  /** Manager per le transazioni e selezioni nel database */
  private GeneManager      geneManager;

  /** Manager per la generazione delle chiavi */
  private GenChiaviManager genChiaviManager;

  /** Manager dei tabellati */
  private TabellatiManager tabellatiManager;

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @return Ritorna geneManager.
   */
  public GeneManager getGeneManager() {
    return geneManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * Controlla la possibilità di eliminare il lavoro e ritorna gli eventuali
   * messaggi bloccanti e non
   *
   * @param codiceLavoro
   *        Codice del lavoro
   * @return Null se è possibile eliminare altrimenti le chiavi dei messaggi
   *         (bloccanti e non, distinti per prefisso)
   * @throws GestoreException
   */
  public String[] getMessageKeysControlliEliminazioneLavoro(String codiceLavoro) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("getMessageKeysControlliEliminazioneLavoro(" + codiceLavoro + "): inizio metodo");

    Vector elenco = new Vector();
    String risultato[] = null;

    Object param[] = new Object[] { codiceLavoro };

    // Verifico che non esistano elaborati definiti
    if (this.geneManager.getLongFromSelect("select count(conta) from elaborat where codlav = ?", param, true) > 0) {
      elenco.add("errors.deleteLavoro.foundElaborati");
    }

    // Verifico che non esistano schede dell'autorita collegate
    if (this.geneManager.getLongFromSelect("select count(nlavsche) from lavsche where codlav = ?", param, true) > 0) {
      elenco.add("errors.deleteLavoro.foundSchedeAutorita");
    }

    // Verifico se esistono lavori derivanti dal lavoro in input
    if (this.geneManager.getLongFromSelect("select count(codlav) from peri where codprg = ? or pcodpr = ? or codstf = ?", new Object[] {
        codiceLavoro, codiceLavoro, codiceLavoro }, true) > 0) {
      elenco.add("warnings.deleteLavoro.foundLavoriDerivanti");
    }

    // Verifico se esistono interventi sul lavoro
    if (this.geneManager.esisteTabella("ITC")
        && this.geneManager.getLongFromSelect("select count(numitc) from itc where codlav = ?", param, true) > 0) {
      elenco.add("warnings.deleteLavoro.foundInterventi");
    }

    if (elenco.size() > 0) risultato = (String[]) elenco.toArray(new String[0]);

    if (logger.isDebugEnabled()) logger.debug("getMessageKeysControlliEliminazioneLavoro(" + codiceLavoro + "): fine metodo");

    return risultato;
  }

  /**
   * Controlla la possibilità di eliminare l'appalto e ritorna gli eventuali
   * messaggi bloccanti e non
   *
   * @param codiceLavoro
   *        Codice del lavoro
   * @param numeroAppalto
   *        numero dell'appalto
   * @return Null se è possibile eliminare altrimenti le chiavi dei messaggi
   *         (bloccanti e non, distinti per prefisso)
   * @throws GestoreException
   */
  public String[] getMessageKeysControlliEliminazioneAppalto(String codiceLavoro, int numeroAppalto) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("getMessageKeysControlliEliminazioneAppalto(appalto "
          + numeroAppalto
          + " del lavoro "
          + codiceLavoro
          + "): inizio metodo");

    Vector elenco = new Vector();
    String risultato[] = null;

    Object param[] = new Object[] { codiceLavoro, new Long(numeroAppalto) };

    // Verifico se esistono interventi sul lavoro
    if (this.geneManager.esisteTabella("ITC")
        && this.geneManager.getLongFromSelect("select count(numitc) from itc where codlav = ? and nappal = ?", param, true) > 0) {
      elenco.add("warnings.deleteAppalto.foundInterventi");
    }

    if (this.geneManager.esisteTabella("ELABORAT")
        && this.geneManager.getLongFromSelect("select count(CONTA) from ELABORAT where CODLAV = ? and NAPPAL = ? and TIPELA = 4", param,
            true) > 0) {
      elenco.add("errors.deleteAppalto.foundElaborati");
    }

    if (elenco.size() > 0) risultato = (String[]) elenco.toArray(new String[0]);

    if (logger.isDebugEnabled())
      logger.debug("getMessageKeysControlliEliminazioneAppalto(appalto " + numeroAppalto + " del lavoro " + codiceLavoro + "): fine metodo");
    return risultato;
  }

  /**
   * Elimina un lavoro e tutte le informazioni collegate
   *
   * @param codiceLavoro
   *        Codice del lavoro da eliminare
   * @throws GestoreException
   */
  public void deleteLavoro(String codiceLavoro) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("deleteLavoro(" + codiceLavoro + "): inizio metodo");

    // Sbianco tutti i lavori derivanti se presenti
    this.sbiancaDerivantiLavoro(codiceLavoro, -1);

    // Elimino tutti gli interventi collegati al lavoro
    if (this.geneManager.esisteTabella("ITC")) {
      this.deleteInterventiAppalto(codiceLavoro, null);
    }
    // Elimino le occorrenze di "CRONOIMPO", "CRONOATTI"
    this.geneManager.deleteTabelle(new String[] { "CRONOIMPO", "CRONOATTI" }, "ncrono in (select ncrono from crono where codlav = ?)",
        new Object[] { codiceLavoro });

    // Cancellazione del collegamento appalti-gare
    if (this.geneManager.esisteTabella("GARE")) {
      try {
        List listaAppaltiLavoro = this.getGeneManager().getSql().getListHashMap("select NAPPAL from APPA where CODLAV = ? ",
            new Object[] { codiceLavoro });

        if (listaAppaltiLavoro != null && listaAppaltiLavoro.size() > 0) {
          for (int i = 0; i < listaAppaltiLavoro.size(); i++) {
            Long numeroAppalto = (Long) ((JdbcParametro) ((HashMap) listaAppaltiLavoro.get(i)).get("NAPPAL")).getValue();
            this.deleteCollegamentoGara(codiceLavoro, numeroAppalto);
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'estrarre la lista degli appalti " + "del lavoro " + codiceLavoro, null, e);
      }
    }

    // Eseguo l'eliminazione di tutte le tabelle collegate al lavoro
    this.geneManager.deleteTabelle(TabelleDelete.getInstance().getElencoTabelle(TabelleDelete.DELETE_PERI), "codlav = ? ",
        new Object[] { codiceLavoro });

    // Cancellazione delle comunicazioni associate al lavoro
    this.deleteComunicazioni("PERI", codiceLavoro, null);

    if (logger.isDebugEnabled()) logger.debug("deleteLavoro(" + codiceLavoro + "): fine metodo");
  }

  /**
   * Elimina un appalto e tutte le informazioni collegate
   *
   * @param codiceLavoro
   *        Codice del lavoro dell'appalto da eliminare
   * @param numeroAppalto
   *        Numero di appalto
   * @throws GestoreException
   */
  public void deleteAppalto(String codiceLavoro, Long numeroAppalto) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("deleteAppalto(" + codiceLavoro + ", " + numeroAppalto + "): inizio metodo");

    // Elimino tutti gli interventi collegati all'appalto
    if (this.geneManager.esisteTabella("ITC")) {
      this.deleteInterventiAppalto(codiceLavoro, numeroAppalto);
    }

    // si eliminano i dati collegati all'appalto
    this.geneManager.deleteTabelle(TabelleDelete.getInstance().getElencoTabelle(TabelleDelete.DELETE_APPA), "codlav = ? and nappal = ?",
        new Object[] { codiceLavoro, numeroAppalto });

    if (this.geneManager.esisteTabella("GARE")) this.deleteCollegamentoGara(codiceLavoro, numeroAppalto);

    // Cancellazione delle fatture relative all'appalto
    try {
      this.geneManager.getSql().update(
          "delete from BGLPAG where CODLAV = ? and NUMCOL in (select NAPPAL from COLLAUD where CODLAV = ? and NAPPAL1 = ?)",
          new Object[] { codiceLavoro, codiceLavoro, numeroAppalto });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione delle fatture " + "associate all'appalto in cancellazione", null, e);
    }

    // Cancellazione del collaudo relativo all'appalto
    try {
      this.geneManager.getSql().update("delete from COLLAUD where CODLAV = ? and NAPPAL1 = ?", new Object[] { codiceLavoro, numeroAppalto });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione del collaudo " + "associato all'appalto in cancellazione", null, e);
    }

    // Cancellazione delle comunicazioni associate all'appalto
    this.deleteComunicazioni("APPA", codiceLavoro, numeroAppalto.toString());

    if (logger.isDebugEnabled()) logger.debug("deleteAppalto(" + codiceLavoro + ", " + numeroAppalto + "): fine metodo");
  }

  /**
   * Elimina un'approvazione/perizia e tutte le informazioni collegate
   *
   * @param codiceLavoro
   *        Codice del lavoro dell'approvazione/perizia da eliminare
   * @param numeroApprovazione
   *        Numero di approvazione
   * @throws GestoreException
   */
  public void deleteApprovazione(String codiceLavoro, Long numeroApprovazione) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("deleteApprovazione(" + codiceLavoro + "," + numeroApprovazione + "): inizio metodo");

    // si eliminano i dati collegati all'approvazione/perizia
    this.geneManager.deleteTabelle(TabelleDelete.getInstance().getElencoTabelle(TabelleDelete.DELETE_APPR), "codlav = ? and naprpr = ?",
        new Object[] { codiceLavoro, numeroApprovazione });

    if (logger.isDebugEnabled()) logger.debug("deleteApprovazione(" + codiceLavoro + "," + numeroApprovazione + "): fine metodo");
  }

  /**
   * Elimina un SAL e tutte le informazioni collegate
   *
   * @param codiceLavoro
   *        Codice del lavoro del SAL da eliminare
   * @param numeroAppalto
   *        Numero di appalto
   * @param numeroSAL
   *        Numero di SAL
   * @throws GestoreException
   */
  public void deleteSAL(String codiceLavoro, Long numeroAppalto, Long numeroSAL) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("deleteSAL(" + codiceLavoro + "," + numeroAppalto + "," + numeroSAL + "): inizio metodo");

    // si eliminano i dati collegati al SAL, ovvero le fatture
    this.geneManager.deleteTabelle(TabelleDelete.getInstance().getElencoTabelle(TabelleDelete.DELETE_PSAL),
        "codlav = ? and nappal = ? and numsal = ?", new Object[] { codiceLavoro, numeroAppalto, numeroSAL });

    if (logger.isDebugEnabled()) logger.debug("deleteSAL(" + codiceLavoro + "," + numeroAppalto + "," + numeroSAL + "): fine metodo");
  }

  /**
   * Elimina le figlie del subappalto
   *
   * @param codiceLavoro
   *        Codice del lavoro
   * @param numeroAppalto
   *        Numero appalto
   * @param numeroSubappalto
   *        Numero susbappalto
   * @throws GestoreException
   */
  public void deleteSUBA(String codiceLavoro, Long numeroAppalto, Long numeroSubappalto) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("deleteSUBA(" + codiceLavoro + "," + numeroAppalto + "," + numeroSubappalto + "): inizio metodo");

    this.geneManager.deleteTabelle(TabelleDelete.getInstance().getElencoTabelle(TabelleDelete.DELETE_SUBA),
        "codlav = ? and nappal = ? and numprs = ?", new Object[] { codiceLavoro, numeroAppalto, numeroSubappalto });

    if (logger.isDebugEnabled())
      logger.debug("deleteSUBA(" + codiceLavoro + "," + numeroAppalto + "," + numeroSubappalto + "): fine metodo");
  }

  /**
   * Elimina un contratto e tutte le informazioni collegate. Inoltre sbianca il
   * campo numero contratto (CODCONT) nelle variazioni tempo e ricalcola la
   * percentuale avanzamento lavori
   *
   * @param codiceLavoro
   *        Codice del lavoro del contratto da eliminare
   * @param numeroAppalto
   *        Numero di appalto
   * @param numeroContratto
   *        Numero di contratto
   * @throws GestoreException
   */
  public void deleteContratto(String codiceLavoro, Long numeroAppalto, Long numeroContratto) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("deleteContratto(" + codiceLavoro + "," + numeroAppalto + "," + numeroContratto + "): inizio metodo");

    try {
      // si sbianca il numero contratto nelle variazioni tempo
      String sql = "update temp1 set codcont = ? where codlav = ? and nappal = ? and codcont = ?";
      this.geneManager.getSql().update(sql, new Object[] { null, codiceLavoro, numeroAppalto, numeroContratto });
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'update per lo svuotamento del codcont per CODLAV="
          + codiceLavoro
          + ", NAPPAL="
          + numeroAppalto
          + ", CODCONT="
          + numeroContratto, "updateCodcont", new Object[] { codiceLavoro, numeroAppalto, numeroContratto }, e);
    }

    // si aggiorna la percentuale di avanzamento lavori
    this.updatePercentualeAvanzamentoLavori(codiceLavoro, numeroAppalto, numeroContratto, "DEL");

    // si eliminano i dati collegati al contratto
    this.geneManager.deleteTabelle(TabelleDelete.getInstance().getElencoTabelle(TabelleDelete.DELETE_CONT),
        "codlav = ? and nappal = ? and nproat = ?", new Object[] { codiceLavoro, numeroAppalto, numeroContratto });

    if (logger.isDebugEnabled())
      logger.debug("deleteContratto(" + codiceLavoro + "," + numeroAppalto + "," + numeroContratto + "): fine metodo");
  }

  /**
   * Metodo per la cancellazione delle comunicazione alle
   * ditte concorrenti
   * @param coment - Entità associata (LAVO o APPA)
   * @param comkey1 - Valore del campo chiave (CODLAV.LAVO e CODLAV.APPA)
   * @param comkey2 - Valore del campo chiave (NAPPAL.APPA)
   * @throws GestoreException
   */
  public void deleteComunicazioni(String coment, String comkey1, String comkey2) throws GestoreException {
    if (logger.isDebugEnabled())
    	logger.debug("deleteComunicazioni(" + coment + "," + comkey1 + "," + comkey2 +
    			"): inizio metodo");

    try {
    	List<?> datiW_INVCOM = null;
    	if (coment.equals("PERI")) {
    		datiW_INVCOM = this.geneManager.getSql().getListVector(
    		          "select idprg, idcom from w_invcom where (coment = 'PERI' or coment = 'APPA') and comkey1 = ?",
    		          new Object[] {comkey1});
    	} else if (coment.equals("APPA")) {
    		datiW_INVCOM = this.geneManager.getSql().getListVector(
  		          "select idprg, idcom from w_invcom where coment = 'APPA' and comkey1 = ? and comkey2 = ?",
  		          new Object[] {comkey1, comkey2});
    	}


      if (datiW_INVCOM != null && datiW_INVCOM.size() >0) {
        for (int i = 0; i < datiW_INVCOM.size(); i++) {
          String idprg = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 0).getValue();
          Long idcom = (Long) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 1).getValue();

          this.geneManager.deleteTabelle(new String[] { "W_INVCOMDES" },
          		"idprg = ? and idcom = ?", new Object[] { idprg,idcom });

          this.geneManager.deleteTabelle(new String[] { "W_DOCDIG" },
          		"digkey1 = ? and digkey2 = ? and digent = 'W_INVCOM'",
              new Object[] { idprg,idcom.toString() });
        }
        if (coment.equals("PERI")) {
        	this.geneManager.deleteTabelle(new String[] { "W_INVCOM" },
            		"(coment = 'PERI' or coment = 'APPA') and comkey1 = ?", new Object[] {  comkey1 });
        } else if (coment.equals("APPA")) {
        	this.geneManager.deleteTabelle(new String[] { "W_INVCOM" },
            		"coment = 'APPA' and comkey1 = ? and comkey2 = ?", new Object[] { comkey1, comkey2 });
        }

      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la cancellazione della comunicazione",null, e);
    }

    if (logger.isDebugEnabled())
    	logger.debug("deleteComunicazioni(" + coment + "," + comkey1 + "," + comkey2 +
    			"): fine metodo");
  }

  /**
   * Sbianca i riferimenti ai lavori derivanti per un determinato lavoro
   *
   * @param codiceLavoro
   *        Codice del lavoro
   * @param newLivpro
   *        Nuovo livello di progettazione che viene settato sul lavoro. -1 se
   *        non va gestita la modifica del livpro
   * @throws GestoreException
   */
  public void sbiancaDerivantiLavoro(String codiceLavoro, int newLivpro) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("sbiancaDerivantiLavoro(" + codiceLavoro + "," + newLivpro + "): inizio metodo");

    try {
      // update a null delle colonne
      for (int i = 0; i < 3; i++) {
        String lsUpdate = "Update peri set "
            + UtilityConversioneCampi.getCampoDerivanteDaLivpro(i)
            + " = ?"
            + " where "
            + UtilityConversioneCampi.getCampoDerivanteDaLivpro(i)
            + "= ?";
        this.geneManager.getSql().update(lsUpdate, new Object[] { null, codiceLavoro });
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'update per lo svuotamento dei lavori derivanti dal lavoro " + codiceLavoro,
          "updateDerivLavoro", new Object[] { codiceLavoro }, e);
    }

    // Se viene cambiato il livello di progettazione allora modifico i tipi
    // derivanti
    if (newLivpro >= 0) {
      try {
        int prgLav = UtilityConversioneCampi.livpro2prglav(newLivpro);
        // Update delle fasi
        this.geneManager.getSql().update("update fasi set prglav = ? where codlav= ?", new Object[] { new Long(prgLav), codiceLavoro });
        // Update dei cronoprogrammi
        this.geneManager.getSql().update("update crono set prglav = ? where codlav= ?", new Object[] { new Long(prgLav), codiceLavoro });
        // Settaggio di tipese se esiste la tabella elaborat
        if (geneManager.esisteTabella("ELABORAT")) {
          int tipese = UtilityConversioneCampi.livpro2tipese(newLivpro);
          this.geneManager.getSql().update("update elaborat set tipese = ? where codlav = ?",
              new Object[] { new Long(tipese), codiceLavoro });
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore durante l'update per la modifica dei tipi derivanti "
            + "il livello di progettazione a partire dal lavoro "
            + codiceLavoro, "updateLivpro", new Object[] { codiceLavoro }, e);
      }
    }
    if (logger.isDebugEnabled()) logger.debug("sbiancaDerivantiLavoro(" + codiceLavoro + "," + newLivpro + "): fine metodo");
  }

  /**
   * Elimina gli interventi collegati ad un appalto di un lavoro
   *
   * @param codiceLavoro
   *        codice del lavoro da eliminare
   * @param numeroAppalto
   *        numero di appalto; se chiamato dal lavoro, allora numeroAppalto è
   *        null
   * @throws GestoreException
   */
  private void deleteInterventiAppalto(String codiceLavoro, Long numeroAppalto) throws GestoreException {
    try {
      String sql = "select numitc from itc where codlav = ?";
      List elencoInterventi = null;
      if (numeroAppalto == null)
        elencoInterventi = this.geneManager.getSql().getListVector(sql, new Object[] { codiceLavoro });
      else
        elencoInterventi = this.geneManager.getSql().getListVector(sql + " and nappal = ?", new Object[] { codiceLavoro, numeroAppalto });

      if (elencoInterventi != null && elencoInterventi.size() > 0) {
        // ciclo sugli interventi per eseguire le eliminazioni singole
        for (int i = 0; i < elencoInterventi.size(); i++) {
          JdbcParametro numeroIntervento = SqlManager.getValueFromVectorParam(elencoInterventi.get(i), 0);
          this.deleteIntervento(numeroIntervento.longValue(), true);
        }
      }

    } catch (SQLException e) {
      if (numeroAppalto == null)
        throw new GestoreException("Errore durante l'estrazione degli interventi del lavoro " + codiceLavoro, "selectInterventiLavoro",
            new Object[] { codiceLavoro }, e);
      else
        throw new GestoreException("Errore durante l'estrazione degli interventi del lavoro "
            + codiceLavoro
            + " e appalto "
            + numeroAppalto.longValue(), "selectInterventiAppalto", new Object[] { codiceLavoro, numeroAppalto }, e);
    }
  }

  /**
   * Elimina i dati relativi ad un intervento ed ai suoi rapportini collegati
   *
   * @param numeroIntervento
   *        numero di intervento da rimuovere
   * @param deleteEntita
   *        se true elimina anche l'occorrenza in ITC, se false non la cancella
   * @throws GestoreException
   */
  private void deleteIntervento(Long numeroIntervento, boolean deleteEntita) throws GestoreException {
    try {
      // si eliminano i rapportini
      List elencoRapint = this.geneManager.getSql().getListVector("select numrap from rapint where numitc=?",
          new Object[] { numeroIntervento });

      if (elencoRapint != null && elencoRapint.size() > 0) {
        // ciclo sui numrap per eliminare le occorrenze in matrap e operap
        for (int i = 0; i < elencoRapint.size(); i++) {
          JdbcParametro numrap = SqlManager.getValueFromVectorParam(elencoRapint.get(i), 0);

          // Eseguo l'eliminazione dei rapportini collegati all'intervento
          this.geneManager.deleteTabelle(TabelleDelete.getInstance().getElencoTabelle(TabelleDelete.DELETE_RAPPORTINI_ITC), "numrap = ? ",
              new Object[] { numrap.longValue() });
        }
      }

      // si eliminano i dati collegati all'intervento
      this.geneManager.deleteTabelle(TabelleDelete.getInstance().getElencoTabelle(TabelleDelete.DELETE_ITC), "numitc = ? ",
          new Object[] { numeroIntervento });

      if (deleteEntita) this.geneManager.deleteTabelle(new String[] { "ITC" }, "numitc = ? ", new Object[] { numeroIntervento });

    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'estrazione dell'intervento " + numeroIntervento.longValue() + " da eliminare",
          "selectIntervento", e);
    }
  }

  /**
   * Ricalcola ed aggiorna la percentuale di avanzamento lavori nell'appalto
   * (campo PERCAL) applicando l'importo netto dell'ultimo contratto
   * dell'appalto con tale importo non nullo
   *
   * @param codiceLavoro
   *        codice del lavoro
   * @param numeroAppalto
   *        numero di appalto
   * @param numeroContratto
   *        numero di contratto
   * @param modalitaChiamante
   *        modalità dell'operazione da cui viene richiamata (INS, UPD, DEL)
   * @throws GestoreException
   */
  public void updatePercentualeAvanzamentoLavori(String codiceLavoro, Long numeroAppalto, Long numeroContratto, String modalitaChiamante)
      throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("updatePercentualeAvanzamentoLavori("
          + codiceLavoro
          + ","
          + numeroAppalto
          + ","
          + numeroContratto
          + ","
          + modalitaChiamante
          + "): inizio metodo");
    aggPERCAL(new DataColumnContainer(this.geneManager.getSql(), "CONT",
        "select codlav, nappal, nproat from cont where codlav = ? and nappal = ? and nproat = ?", new Object[] { codiceLavoro,
            numeroAppalto, numeroContratto }), modalitaChiamante);

    if (logger.isDebugEnabled())
      logger.debug("updatePercentualeAvanzamentoLavori("
          + codiceLavoro
          + ","
          + numeroAppalto
          + ","
          + numeroContratto
          + ","
          + modalitaChiamante
          + "): fine metodo");
  }

  /**
   * Estrae da un tabellato (Ag006) il valore dell'IVA
   *
   * @return IVA in centesimi (100 = 100%, 20 = 20%)
   */
  public Double getTabellatoIVA() throws GestoreException {
    Double iva = null;
    try {
      Vector ret = this.geneManager.getSql().getVector("Select tab1desc From tab1 Where tab1cod = ?", new Object[] { "Ag006" });
      if (ret != null && ret.size() > 0) {
        String val = UtilityStringhe.replace(ret.get(0).toString(), ",", ".").trim();
        iva = new Double(Double.parseDouble(val) * 100.);
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il calcolo dell'IVA", "tabellatoIVA", e);
    }
    return iva;
  }

  /**
   * Esegue l'aggiornamento della classificazione per gli appalti e ordini
   *
   * @param codlav
   *        Codice del lavoro
   * @param interv
   *        Natura intervento
   * @param catint1
   *        Sotto natura intervento
   * @param claint
   *        Categoria intervento
   * @param setint
   *        Sottocategoria intervento
   * @param tipopr
   *        Tipologia intervento
   * @throws GestoreException
   */
  public void updateCatintInAppaOrdi(String codlav, String interv, String catint1, String claint, String setint, String tipopr)
      throws GestoreException {
    String catint = calcoloCategoriaIntervento(interv, catint1, claint, setint, tipopr);
    // Eseguo gli update su appalti e forniture
    try {
      this.geneManager.getSql().update("Update appa set catint = ? where codlav = ? and annavv is null", new Object[] { catint, codlav });
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'update della categoria dell'intervento a '"
          + catint
          + "' in APPA per il lavoro '"
          + codlav
          + "'", "updateCatintAppa", new Object[] { codlav }, e);
    }
    try {
      this.geneManager.getSql().update("Update ordi set catint = ? where codlav = ? and annavv is null", new Object[] { catint, codlav });
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'update della categoria dell'intervento a '"
          + catint
          + "' in ORDI per il lavoro '"
          + codlav
          + "'", "updateCatintOrdi", new Object[] { codlav }, e);
    }
  }

  /**
   * @param interv
   * @param catint1
   * @param claint
   * @param setint
   * @param tipopr
   * @return
   */
  public String calcoloCategoriaIntervento(String interv, String catint1, String claint, String setint, String tipopr) {
    return UtilityStringhe.fillRight(UtilityStringhe.convertiNullInStringaVuota(interv), ' ', 3)
        + UtilityStringhe.fillRight(UtilityStringhe.convertiNullInStringaVuota(catint1), ' ', 2)
        + UtilityStringhe.fillRight(UtilityStringhe.convertiNullInStringaVuota(claint), ' ', 3)
        + UtilityStringhe.fillRight(UtilityStringhe.convertiNullInStringaVuota(setint), ' ', 1)
        + UtilityStringhe.fillRight(UtilityStringhe.convertiNullInStringaVuota(tipopr), ' ', 2);
  }

  /**
   * Esegue l'aggiornamento del livello di progettazione superato
   *
   * @param codlav
   *        Codice del lavoro di partenza
   * @param codstf
   *        Codice studio di fattibilità
   * @param codprg
   *        Codice progetto preliminare
   * @param pcodpr
   *        Codice progetto definitivo
   * @throws GestoreException
   */
  public void updateLivsup(String codlav, String codstf, String codprg, String pcodpr) throws GestoreException {
    Vector parametri = new Vector();
    String sqlParametri = "";
    if (codstf != null) parametri.addElement(codstf);
    if (codprg != null) parametri.addElement(codprg);
    if (pcodpr != null) parametri.addElement(pcodpr);
    // si costruisce l'array di parametri e l'sql con i "?" per la prepared
    // statement
    if (parametri.size() > 0) {
      String[] arrayParametri = (String[]) parametri.toArray(new String[0]);
      for (int i = 0; i < parametri.size(); i++) {
        if (i == 0)
          sqlParametri = "(";
        else
          sqlParametri += ",";
        sqlParametri += "?";
      }
      sqlParametri += ")";

      try {
        this.geneManager.getSql().update("Update peri set livsup = 1 where codlav in " + sqlParametri, arrayParametri);
      } catch (SQLException e) {
        throw new GestoreException("Errore durante l'update del livsup a 1 per i lavori con codice '"
            + codstf
            + "', '"
            + codprg
            + "', '"
            + pcodpr
            + "'", "updateLivsup", new Object[] { codlav }, e);
      }
    }
  }

  /**
   * Esegue l'aggiornamento dei titoli degli appalti il cui titolo non è
   * valorizzato
   *
   * @param codlav
   *        Codice del lavoro
   * @param titsil
   *        Titolo del lavoro
   * @throws GestoreException
   */
  /*
   * public void updateTitoliAppalti(String codlav, String titsil) throws
   * GestoreException { try { this.geneManager.getSql().update(
   * "Update appa set notapp = ? where codlav = ? and notapp is null", new
   * Object[] { titsil, codlav }); } catch (SQLException e) { throw new
   * GestoreException( "Errore durante l'update dei titoli degli appalti a '" +
   * titsil + "' in APPA per il lavoro '" + codlav + "'", "updateTitoliAppalti",
   * new Object[] { codlav }, e); } }
   */

  /**
   * Inserisce un'occorrenza nella tabella DATICUP in modo da replicare nel
   * lavoro i dati dell'intervento associato al lavoro stesso
   *
   * @param codiceLavoro
   *        lavoro
   * @param contri
   *        progressivo piano triennale
   * @param conint
   *        progressivo intervento
   * @throws GestoreException
   */
  public void insertDatiCUP(String codlav, Long contri, Long conint) throws GestoreException {
    try {
      // prima si elimina l'occorrenza (se esiste) nella daticup per il lavoro
      this.geneManager.getSql().update("delete from daticup where codlav = ?", new Object[] { codlav });

      // poi si estraggono (se esistono) i dati cup dell'intervento
      DataColumnContainer gestoreDatiCUP = new DataColumnContainer(this.geneManager.getSql(), "DATICUP",
          "select * from daticup where contri=? and conint=?", new Object[] { contri, conint });

      // si si arriva qui, allora esiste un'occorrenza nella dati cup, quindi si
      // calcola una nuova chiave...
      int progressivo = this.genChiaviManager.getMaxId("DATICUP", "NUMPRO");

      // ...si svuota l'originalValue di TUTTI i campi della DATICUP nel
      // gestore, altrimenti non è possibile riusarli per la insert...
      DataColumn[] colonne = gestoreDatiCUP.getColumns("DATICUP", 0);
      for (int i = 0; i < colonne.length; i++)
        colonne[i].setOriginalValue(null);

      // ...e poi si settano alcune informazioni e si richiede l'insert dei dati
      gestoreDatiCUP.setValue("DATICUP.NUMPRO", new Long(progressivo + 1));
      gestoreDatiCUP.setValue("DATICUP.CONTRI", null);
      gestoreDatiCUP.setValue("DATICUP.CONINT", null);
      gestoreDatiCUP.setValue("DATICUP.CODLAV", codlav);
      gestoreDatiCUP.insert("DATICUP", this.geneManager.getSql());
    } catch (GestoreException e) {
      logger.warn("Viene solo eliminata l'eventuale occorrenza in DATICUP per il lavoro '"
          + codlav
          + "' in quanto non esistono i dati cup per l'intervento ("
          + contri.longValue()
          + ", "
          + conint.longValue()
          + ")", e);
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'update dei titoli degli appalti a '" + codlav + "'", "updateDatiCUP",
          new Object[] { codlav }, e);
    }
  }

  /**
   * Aggiorna la percentuale d'avanzamento dei lavori del contratto
   *
   * @param impl
   * @param modo
   *        "DEL" se è in eliminazione altrimenti si tratta di update
   */
  public void aggPERCAL(DataColumnContainer impl, String modo) throws GestoreException {
    // Eseguo l'aggiornamento solo se ci sono state modifiche

    if (impl.isColumn("CONT.CODLAV") && impl.isColumn("CONT.NAPPAL") && impl.isColumn("CONT.NPROAT")) {
      String percal = null;
      String codlav = impl.getString("CONT.CODLAV");
      Long nappal = impl.getLong("CONT.NAPPAL");
      Long nproat = impl.getLong("CONT.NPROAT");
      long ultimoContratto;
      try {
        if ("DEL".equalsIgnoreCase(modo))
          ultimoContratto = getUltimoContratto(codlav, nappal, "nimpco Is Not Null And nproat <> ?", new Object[] { nproat });
        else
          ultimoContratto = getUltimoContratto(codlav, nappal, "nimpco Is Not Null", null);
        if (ultimoContratto != 0) {
          if ("DEL".equalsIgnoreCase(modo) || (nproat.longValue() >= ultimoContratto)) {

            Double nimpco = impl.getCampoDouble(this.geneManager.getSql(), "CONT.NIMPCO", "codlav = ? And nappal = ? And nproat = ?",
                new Object[] { codlav, nappal, new Long(ultimoContratto) });

            if (nimpco != null && nimpco.doubleValue() != 0) {
              Double impusal = impl.getCampoDouble(this.geneManager.getSql(), "APPA.IMPUSAL", "codlav = ? And nappal = ?", new Object[] {
                  codlav, nappal });
              if (impusal != null) {
                percal = String.valueOf(UtilityMath.round(impusal.doubleValue() * 100 / nimpco.doubleValue(), 2));
                if (percal.length() > 15) percal = percal.substring(0, 15);
              }
            }
            impl.updateCampo(this.geneManager.getSql(), "APPA.PERCAL", percal, "codlav = ? And nappal = ?", new Object[] { codlav, nappal });
          }
        }

      } catch (Throwable e) {
        throw new GestoreException("Errore nell'aggiornamento della percentuale avanzamento contratto per il lavoro " + codlav,
            "updatePercal", e);
      }

    }

  }

  /**
   * Funzione ce setta l'importo netto per le categorie prevalenti ed ulteriori
   * dell'appalto
   *
   * @param impl
   *        Impl con le colonne
   * @param codlav
   *        Codice lavoro
   * @param nappal
   *        Numero di appalto
   * @param nompco
   *        Importo netto contrattuale
   * @param valido
   *        Flag per dire se l'importo netto è valido o se deve essere letto dal
   *        contratto
   */
  public void setImportoNettoCategorie(DataColumnContainer impl, String codlav, Long nappal, Double nimpco, boolean valido)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("setImportoNettoCategorie(impl, " + codlav + "," + nappal + "," + nimpco + "," + valido + "): inizio metodo");

    // Verifico se esistono in db tutte le tabelle
    if (this.geneManager.getSql().isTable("APPA")
        && this.geneManager.getSql().isTable("CONT")
        && this.geneManager.getSql().isTable("CATAPP")
        && this.geneManager.getSql().isTable("ULTAPP")) {
      try {
        Double implav = impl.getCampoDouble(this.geneManager.getSql(), "APPA.IMPLAV", "codlav = ? and nappal= ?", new Object[] { codlav,
            nappal });
        if (implav == null) implav = new Double(0);
        Double onprge = impl.getCampoDouble(this.geneManager.getSql(), "APPA.ONPRGE", "codlav = ? and nappal = ?", new Object[] { codlav,
            nappal });
        if (onprge == null) onprge = new Double(0);
        double diffImplavOnprge = implav.doubleValue() - onprge.doubleValue();
        if (diffImplavOnprge == 0) {
          // Se la differenza è 0 allora setto a null l'importo di catapp e
          // ultapp
          this.geneManager.getSql().update("update catapp set impnco = null where codlav = ? and nappal = ? and ncatg = 1",
              new Object[] { codlav, nappal });
          this.geneManager.getSql().update("update ultapp set impnco = null where codlav = ? and nappal = ? ",
              new Object[] { codlav, nappal });
        } else {
          // La differenza è diversa da 0
          // Se nimpco non è valido allora estraggo quello dell'ultimo atto
          // approvato
          if (!valido) {
            Long ultimoContratto = new Long(getUltimoContratto(codlav, nappal, "daatto is not null", null));
            if (ultimoContratto.longValue() > 0) {
              Vector ret = this.geneManager.getSql().getVector("select nimpco from cont where codlav=? and nappal=? and nproat=?",
                  new Object[] { codlav, nappal, ultimoContratto });
              nimpco = SqlManager.getValueFromVectorParam(ret, 0).doubleValue();
            }
            if (nimpco == null) nimpco = new Double(0);
          }
          Double impbasg = impl.getCampoDouble(this.geneManager.getSql(), "CATAPP.IMPBASG", "codlav = ? and nappal = ? and ncatg = 1",
              new Object[] { codlav, nappal });
          if (impbasg == null) impbasg = new Double(0);
          Double impnco = new Double(UtilityMath.round(impbasg.doubleValue()
              * (nimpco.doubleValue() - onprge.doubleValue())
              / (diffImplavOnprge), 2));
          if (impnco.doubleValue() == 0) impnco = null;
          // Eseguo l'update su catapp
          this.geneManager.getSql().update("update CATAPP set IMPNCO = ? where codlav = ? and nappal = ? and ncatg = 1",
              new Object[] { impnco, codlav, nappal });
          List lUltapp = this.geneManager.getSql().getListVector("SELECT impapo, nopega FROM ultapp WHERE codlav = ? and nappal = ?",
              new Object[] { codlav, nappal });
          for (Iterator iter = lUltapp.iterator(); iter.hasNext();) {
            Object row = iter.next();
            Double impapo = SqlManager.getValueFromVectorParam(row, 0).doubleValue();

            impnco = new Double(UtilityMath.round(
                (impapo == null ? 0 : impapo.doubleValue()) * (nimpco.doubleValue() - onprge.doubleValue()) / (diffImplavOnprge), 2));
            if (impnco.doubleValue() == 0) impnco = null;
            // Eseguo l'update
            this.geneManager.getSql().update("update ultapp set impnco=? where codlav=? and nappal=? and nopega=?",
                new Object[] { impnco, codlav, nappal, SqlManager.getValueFromVectorParam(row, 1).longValue() });
          }

        }
      } catch (Throwable t) {
        throw new GestoreException("Errore nell'aggiornamento della categorie dell'appalto " + nappal + " per il lavoro " + codlav,
            "updateCateg", t);
      }
    }

    if (logger.isDebugEnabled())
      logger.debug("setImportoNettoCategorie(impl, " + codlav + "," + nappal + "," + nimpco + "," + valido + "): fine metodo");
  }

  /**
   * Estrae l'ultimo contratto
   *
   * @param codlav
   * @param nappal
   * @param where
   *        Where da aggiungere
   * @param paramsAdd
   *        Eventuali parametri da aggiungere a tutti i parametri
   * @return
   */
  public long getUltimoContratto(String codlav, Long nappal, String where, Object[] paramsAdd) throws GestoreException {
    Vector ret = null;
    StringBuffer buf = new StringBuffer("select max(nproat) from cont where codlav = ? and nappal = ?");
    if (where != null && where.length() > 0) {
      buf.append(" and ");
      buf.append(where);
    }
    Object parms[] = null;
    if (paramsAdd != null && paramsAdd.length > 0) {
      parms = new Object[2 + paramsAdd.length];
      parms[0] = codlav;
      parms[1] = nappal;
      for (int i = 0; i < paramsAdd.length; i++)
        parms[i + 2] = paramsAdd[i];
    } else
      parms = new Object[] { codlav, nappal };
    try {
      ret = this.geneManager.getSql().getVector(buf.toString(), parms);
    } catch (SQLException e) {
      throw new GestoreException("Errore in estrazione dell'ultimo contratto per il lavoro " + codlav + ", appalto " + nappal,
          "ultimoContratto", e);
    }
    if (ret != null && ret.size() > 0) {
      Long lRet = SqlManager.getValueFromVectorParam(ret, 0).longValue();
      if (lRet != null) return lRet.longValue();
    }
    return 0;
  }

  /**
   * Funzione che setta l'iscrizione della categoria prevalente dell'appalto
   *
   * @param impl
   * @param codlav
   * @param nappal
   */
  public void updateCategorieIscrizione(DataColumnContainer impl, String codlav, Long nappal) throws GestoreException {
    try {
      Double implav = impl.getCampoDouble(this.geneManager.getSql(), "APPA.IMPLAV", "codlav = ? and nappal = ?", new Object[] { codlav,
          nappal });
      if (implav == null) implav = new Double(0);
      Double onprge = impl.getCampoDouble(this.geneManager.getSql(), "APPA.ONPRGE", "codlav = ? and nappal = ?", new Object[] { codlav,
          nappal });
      if (onprge == null) onprge = new Double(0);
      String catiga = impl.getCampoString(this.geneManager.getSql(), "CATAPP.CATIGA", "codlav = ? and nappal = ?", new Object[] { codlav,
          nappal });
      if (catiga != null && catiga.length() > 0) {
        long tiplav = getTiplavgCais(catiga);
        if (tiplav == 1) {
          double impapo_sum = 0;
          List ret = this.geneManager.getSql().getListVector(
              "SELECT impapo, catoff FROM ultapp WHERE codlav=? and nappal=? and ( (acontec is not null) and (acontec=?) )",
              new Object[] { codlav, nappal, "1" });
          if (ret != null) {
            for (Iterator iter = ret.iterator(); iter.hasNext();) {
              Object row = iter.next();
              // Se si tratta di un tipo per valori allora aggiungo l'importo
              if (getTiplavgCais(SqlManager.getValueFromVectorParam(row, 1).stringValue()) == 1) {
                Double impapo = SqlManager.getValueFromVectorParam(row, 0).doubleValue();
                if (impapo == null) impapo = new Double(0);
                impapo_sum += impapo.doubleValue();
              }
            }
          }
          double imp = implav.doubleValue() - onprge.doubleValue() - impapo_sum;
          updateCategorieIscrizione(imp, codlav, nappal, getTabCatg());
        }
      }
    } catch (Throwable t) {
      throw new GestoreException("Errore nell'impostazione della categoria d'iscrizione", "setIscr", t);
    }

  }

  /**
   * Setta l'importo iscrizione
   *
   * @param impl
   * @param imp
   * @param codlav
   * @param nappal
   * @param tabCatg
   * @throws SQLException
   */
  private void updateCategorieIscrizione(double imp, String codlav, Long nappal, Object[] tabCatg) throws SQLException {
    imp = UtilityMath.round(imp / 1.2, 0);
    for (int i = 0; i < tabCatg.length - 3; i += 3) {
      if (((Double) tabCatg[i + 1]).longValue() >= imp || i > (tabCatg.length - 6)) {
        this.geneManager.getSql().update("update CATAPP set impiga = ?, numcla = ? where codlav = ? and nappal = ? and ncatg = 1",
            new Object[] { tabCatg[i + 1], tabCatg[i], codlav, nappal });
        break;
      }
    }
  }

  /**
   * Funzione che da il tabellato delle categorie nel formato [numeroRomano,
   * importo, descrizione][,numeroRomano, importo, descrizione]*
   *
   * @return
   */
  public Object[] getTabCatg() {
    return new Object[] { new Integer(1), new Double(258228), " I          258'228 €", new Integer(2), new Double(516457),
        " II         516'457 €", new Integer(3), new Double(1032914), " III      1'032'914 €", new Integer(4), new Double(2582284),
        " IV    2'582'284 €", new Integer(5), new Double(5164569), " V     5'164'569 €", new Integer(6), new Double(10329138),
        " VI   10'329'138 €", new Integer(7), new Double(15493707), " VII  15'493'707 €", new Integer(8), new Double(999999999),
        " VIII  illimitata" };
  }

  public long getTiplavgCais(String catiga) throws GestoreException {
    Object tiplav;
    long valore = 0;
    try {
      tiplav = this.geneManager.getSql().getObject("select TIPLAVG from CAIS where caisim = ?", new Object[] { catiga });
      if (tiplav instanceof Long)
        valore = ((Long) tiplav).longValue();
      else if (tiplav instanceof Double)
        valore = ((Double) tiplav).longValue();
      else if (tiplav instanceof Integer) valore = ((Integer) tiplav).longValue();
    } catch (SQLException e) {
      throw new GestoreException("Errore in lettura di TIPLAVG di CAIS", "getTiplavgCais", e);
    }
    return valore;
  }

  /**
   * Funzione che verifica se su un appalto esistono righe di contabilità aperte
   *
   * @param codlav
   * @param nappal
   * @return
   */
  public boolean esistonoRigheLibrettoAperte(String codlav, Long nappal) throws GestoreException {
    try {
      List ret = this.geneManager.getSql().getVector(
          "select 1 from cnrighe, elaborat "
              + "where elaborat.codlav = ? and elaborat.nappal = ? "
              + "and elaborat.tipela = 4 and elaborat.conta = cnrighe.conta "
              + "and ( cnrighe.nord > elaborat.chnord or elaborat.chnord is null )", new Object[] { codlav, nappal });
      if (ret != null && ret.size() > 0) return true;
    } catch (SQLException e) {
      throw new GestoreException("Errore il verifica di righe di libretto aperte !", "verificaRigheLibrettoAperte", e);
    }
    return false;
  }

  /**
   * Funzione che estrae il ribasso dellultimo contratto
   *
   * @param codlav
   *        Codice lavoro
   * @param nappal
   *        Numero dell'appalto
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  public Double getRibassoUltimoCont(String codlav, Long nappal) throws GestoreException {
    try {
      List ret = this.geneManager.getSql().getVector(
          "select riaumi from cont "
              + "where codlav = ? and nappal = ? "
              + "and nproat = (select max(nproat)  from cont where codlav = ? and nappal = ? )",
          new Object[] { codlav, nappal, codlav, nappal });
      if (ret != null && ret.size() > 0) {
        return SqlManager.getValueFromVectorParam(ret, 0).doubleValue();
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore in lettura del ribasso dell'ultimo contrattto !", "ribassoUltimoContratto", e);
    }

    return null;
  }

  public boolean esisteContabilitaInAppalto(String codlav, Long nappal) throws GestoreException {
    try {
      List ret = this.geneManager.getSql().getVector("select * from elaborat where codlav = ? and nappal = ? and tipela = 4",
          new Object[] { codlav, nappal });
      if (ret != null && ret.size() > 0) {
        return true;
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore in verifica esistenza contabilità in appalto !", "isContabilitaInAppalto", e);
    }
    return false;
  }

  /**
   * Funzione che estrae la percentuale di limite della cauzione
   *
   * @return
   */
  public Double getPercentualeLimiteCauzione() throws GestoreException {
    try {
      List ret = this.geneManager.getSql().getVector("select tab1desc from tab1 where tab1cod=? and tab1tip=?",
          new Object[] { "A2170", new Long(1) });
      if (ret != null && ret.size() > 0) {
        String valore = SqlManager.getValueFromVectorParam(ret, 0).stringValue();
        valore = UtilityStringhe.replace(valore, ",", ".");
        try {
          Double retVal = new Double(valore);
          retVal = new Double(UtilityMath.round(retVal.doubleValue(), 9));
          return retVal;
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'estrazione della percentuale limite della cauzione !", "estraiPercentualeLimiteDiCauzione", e);
    }

    return null;
  }

  /**
   * Funzione che esegue l'update dei dati dell'appalto con i dati del sal
   *
   * @param codlav
   * @param nappal
   */
  public void updateDatiAppaltoDaSal(String codlav, Long nappal) throws GestoreException {
    TransactionStatus status = null;
    try {
      Double impusal = null, imprce = null;
      String percal = null;

      List ret = this.geneManager.getSql().getVector(
          "select imprna from psal where codlav = ? and nappal = ? and numsal = (select max(numsal) from psal where codlav = ? and nappal = ? and saluff = '1' )",
          new Object[] { codlav, nappal, codlav, nappal });
      if (ret != null && ret.size() > 0) {
        impusal = SqlManager.getValueFromVectorParam(ret, 0).doubleValue();
        if (impusal != null && impusal.doubleValue() != 0) {
          // Estraggo nimpco
          ret = this.geneManager.getSql().getVector(
              "SELECT NIMPCO FROM CONT WHERE CODLAV = ? AND NAPPAL = ? "
                  + "AND NPROAT =(select max(nproat) from cont where codlav = ? and nappal = ? AND NIMPCO IS NOT NULL)",
              new Object[] { codlav, nappal, codlav, nappal });
          if (ret != null && ret.size() > 0) {
            Double tmp = SqlManager.getValueFromVectorParam(ret, 0).doubleValue();
            if (tmp != null && tmp.doubleValue() != 0) {
              percal = String.valueOf(UtilityMath.round(impusal.doubleValue() * 100 / tmp.doubleValue(), 2));
            }
          }
        }
      }
      ret = this.geneManager.getSql().getVector("SELECT SUM(ICERTP) from psal where codlav = ? and nappal = ? and saluff = '1'",
          new Object[] { codlav, nappal });
      if (ret != null && ret.size() > 0) imprce = SqlManager.getValueFromVectorParam(ret, 0).doubleValue();
      status = this.geneManager.getSql().startTransaction();
      this.geneManager.getSql().update(
          "update appa set " + "impusal = ?, " + "percal = ?, " + "imprce = ? " + "where codlav = ? and nappal = ?",
          new Object[] { impusal, percal, imprce, codlav, nappal });
      this.geneManager.getSql().commitTransaction(status);
    } catch (SQLException e) {
      if (status != null) {
        try {
          this.geneManager.getSql().rollbackTransaction(status);
        } catch (SQLException e1) {
        }
      }
      throw new GestoreException("Errore nell'aggiornamento dei dati dell'appalto dal SAL !", "updateAppaDaSAL", e);
    }

  }

  /**
   * Funzione che esegue
   *
   * @param status
   *        Transaction status
   * @param sorgente
   *        Codice lavoro sorgente
   * @param destinazione
   *        Codice lavoro destinazione
   * @param request
   *        Oggetto request
   * @throws GestoreException
   */
  public void copiaLavoro(TransactionStatus status, String sorgente, String destinazione, HttpServletRequest request)
      throws GestoreException {
    // Come prima cosa verifico la non esistenza del codice lavoro di
    // destinazione
    try {
      List ret = this.geneManager.getSql().getVector("select 1 from peri where codlav = ?", new Object[] { destinazione });
      if (ret != null && ret.size() > 0)
        throw new GestoreException("Il lavoro di destinazione è già esistente !", "copiaLavoro.destinazioneEsistente");
    } catch (SQLException e) {
      throw new GestoreException("Errore nella selezione del lavoro di destinazione !", "copiaLavoro", e);
    }
    // Come prima cosa verifico l'esistenza del codice lavoro sorgente
    try {
      List ret = this.geneManager.getSql().getVector("select 1 from peri where codlav = ?", new Object[] { sorgente });
      if (ret == null || ret.size() == 0)
        throw new GestoreException("Il lavoro sorgente non esiste nella base dati !", "copiaLavoro.sorgenteNonEsistente");
    } catch (SQLException e) {
      throw new GestoreException("Errore nella selezione del lavoro sorgente !", "copiaLavoro", e);
    }
    String entitaCopia[] = { "PERI", "COLLAUD", "TECCOLL", "CAPCOM", "CDCO", "ISCS", "FINPRO", "SOMCONT", "DIRLAV", "DIRTEC", "ESPR",
        "APROG", "ORDI", "G2ORDICDC", "PERI1", "PESP", "BGLPAG", "TEMP1", "APPDIR", "EBPR", "VERPRZ", "NUOPRZ", "IMPE", "APPA", "ORDSERV",
        "CONTAB", "DITT", "SUBA", "RISERVE", "ASSIST", "PSAL", "FATTPAG", "CONT", "G2CONPEG", "APPA1", "IMPE", "APPTRA", "ORDTRA", "APPR",
        "APARE3", "APARE1", "DELI", "APARE10", "APARE8", "AGGI", "SOMM", "IMPAPR", "IMPFIN", "IMPSON", "APARE12", "APARE2", "APARE9",
        "DFINA", "R2IMPF", "R2VERI", "FINA", "APARE11", "APARE14", "APARE13", "APARE7", "APARE6", "APARE4", "APARE5", "ORDTRA", "APPTRA",
        "OPELAV", "DATISTA", "G2OPPU", "G2TECN", "G2ELAB", "G2CDSP", "G2PARE", "FASI", "CRONO", "DATICUP", "CATAPP", "ULTAPP", "G2DURC",
        "RAGDET", "CESSCRED" };
    for (int i = 0; i < entitaCopia.length; i++) {
      if (this.geneManager.getSql().isTable(entitaCopia[i])
          && this.geneManager.countOccorrenze(entitaCopia[i], "codlav = ?", new Object[] { sorgente }) > 0) {
        StringBuffer sql = new StringBuffer("select * from ");
        sql.append(entitaCopia[i]);
        sql.append(" where codlav = ?");
        // Se ci sono occorenze allora eseguo la copia
        DataColumnContainer impl = new DataColumnContainer(this.geneManager.getSql(), entitaCopia[i], sql.toString(),
            new Object[] { sorgente });
        List dati = null;
        try {
          dati = this.geneManager.getSql().getListHashMap(sql.toString(), new Object[] { sorgente });
          if (dati != null && dati.size() > 0) {
            Long maxVal = null;
            for (int row = 0; row < dati.size(); row++) {
              impl.setValoriFromMap((HashMap) dati.get(row), true);
              impl.setValue("CODLAV", destinazione);
              // Gestioni particolari della copia
              if ("APPA".equalsIgnoreCase(entitaCopia[i])) {
                // Appalto
                impl.setValue("CODCUA", null);
              } else if ("CRONO".equalsIgnoreCase(entitaCopia[i])) {
                // Cronoprogramma
                if (maxVal == null) {
                  maxVal = new Long(this.geneManager.getLongFromSelect("select max(ncrono) from crono", null, false));
                }
                maxVal = new Long(maxVal.longValue() + 1);
                // Inserisco anche tutti i cronoatti
                this.geneManager.getSql().update(
                    "insert into  CRONOATTI "
                        + "( NCRONO, NUMATT, DESCATT, GGATT, TCLIMAT, GGCONSEC, ATTPREC, TIPREL, GGREL, DTINIZ, DTFINE, "
                        + "IMPPREV, IMPLAV, NLAVFASE, GGLAV, ATTPADRE, DESCULT, ORDATT, ORDSOT ) "
                        + "select ?, NUMATT, DESCATT, GGATT, TCLIMAT, GGCONSEC, ATTPREC, TIPREL, GGREL, DTINIZ, DTFINE, "
                        + "IMPPREV, IMPLAV, NLAVFASE, GGLAV, ATTPADRE, DESCULT, ORDATT, ORDSOT from CRONOATTI where NCRONO = ?",
                    new Object[] { maxVal, impl.getLong("NCRONO") });
                // Inserisco i cronoimp
                // Inserisco anche tutti i cronoatti
                this.geneManager.getSql().update(
                    "insert into CRONOIMPO "
                        + "( NCRONO, DTIMPO, IMPPREV, IMPLAV, IMPSAL ) "
                        + "select ?, DTIMPO, IMPPREV, IMPLAV, IMPSAL from CRONOIMPO where ncrono = ?",
                    new Object[] { maxVal, impl.getLong("NCRONO") });
                impl.setValue("NCRONO", maxVal);

              } else if ("DATICUP".equalsIgnoreCase(entitaCopia[i])) {
                // Dati CUP
                // Cronoprogramma
                if (maxVal == null) {
                  maxVal = new Long(this.geneManager.getLongFromSelect("select max(numpro) from daticup", null, false));
                }
                maxVal = new Long(maxVal.longValue() + 1);
                impl.setValue("NUMPRO", maxVal);
              } else if ("PERI".equalsIgnoreCase(entitaCopia[i])) {
                // Estraggo un gestore
                AbstractGestoreEntita gest = new DefaultGestoreEntita("PERI", request);
                gest.inserisciPermessi(impl, "CODLAV", new Integer(1));
              } else if ("RAGDET".equalsIgnoreCase(entitaCopia[i])) {
                // Dati RAGDET
                Long maxNumdic;
                maxNumdic = new Long(this.geneManager.getLongFromSelect("select max(numdic) from ragdet where codimp=? and coddic=?",
                    new Object[] { impl.getString("CODIMP"), impl.getString("CODDIC") }, false));

                maxNumdic = new Long(maxNumdic.longValue() + 1);
                impl.setValue("NUMDIC", maxNumdic);
              }
              impl.insert(entitaCopia[i], this.geneManager.getSql());
            }
            // Eseguo la copia delle occorrenze del generatore attributi
            this.geneManager.copiaOccorrenzeGeneratoreAttributi(entitaCopia[i], "codlav = ?", new Object[] { sorgente },
                new String[] { "CODLAV" }, new Object[] { destinazione }, false);
            // Eseguo la copia delle occorrenze delle note avvisi
            this.geneManager.copiaOccorrenzeNoteAvvisi(entitaCopia[i], "codlav = ?", new Object[] { sorgente }, new String[] { "CODLAV" },
                new Object[] { destinazione }, false);
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nella copia del lavoro ! per entità " + entitaCopia[i], "copiaLavoro", e);
        }
      }
    }
  }

  /**
   * Funzione che esegue il rinomina del codice del lavoro
   *
   * @param status
   *        Transaction Status
   * @param sorgente
   *        Codice lavoro sorgente
   * @param destinazione
   *        Codice lavoro di destinazione
   * @param request
   */
  public void rinominaLavoro(String sorgente, String destinazione) throws GestoreException {
    // Come prima cosa verifico la non esistenza del codice lavoro di
    // destinazione
    try {
      List ret = this.geneManager.getSql().getVector("select 1 from peri where codlav = ?", new Object[] { destinazione });
      if (ret != null && ret.size() > 0)
        throw new GestoreException("Il lavoro di destinazione è già esistente !", "rinominaLavoro.destinazioneEsistente");
    } catch (SQLException e) {
      throw new GestoreException("Errore nella selezione del lavoro di destinazione !", "rinominaLavoro", e);
    }
    // Come prima cosa verifico la non esistenza del codice lavoro sorgente
    try {
      List ret = this.geneManager.getSql().getVector("select 1 from peri where codlav = ?", new Object[] { sorgente });
      if (ret == null || ret.size() == 0)
        throw new GestoreException("Il lavoro di destinazione è già esistente !", "rinominaLavoro.sorgenteNonEsistente");
    } catch (SQLException e) {
      throw new GestoreException("Errore nella selezione del lavoro di destinazione !", "rinominaLavoro", e);
    }
    String entita[] = { "PERI", "APPA", "APPA1", "APPR", "CDCO", "CONT", "CONTAB", "DATISTA", "ISCS", "PSAL", "RISBON", "SOMM", "TEMP1",
        "AGGI", "BGLPAG", "COLLAUD", "DELI", "DFINA", "ESPR", "FINA", "R2IMPF", "R2VERI", "G2CDSP", "G2CONPEG", "G2ELAB", "G2PARE",
        "G2TECN", "NUOPRZ", "ORDI", "G2ORDICDC", "ORDSERV", "PESP", "RISERVE", "SUBA", "TECCOLL", "VERPRZ", "G2FONTE", "G2OPPU", "ITCORD",
        "PSALCDP", "ITCSMAT", "FASI", "OPELAV", "CRONO", "CATAPP", "ULTAPP", "G2DURC", "CESSCRED", "PERI.codprg", "PERI.pcodpr",
        "PERI.codstf", "LAVSCHE.codlav", "ITC.codlav", "RAPINT.codlav", "ELABORAT.codlav", "GARE.clavor", "SCHCON.codlav", "PROGET.codlav",
        "DATICUP.codlav", "G_PERMESSI.codlav", "RAGDET.codlav" };
    // Delle tabella LAVSCHE, ITC, RAPINT, ELABORAT, GARE, SCHCON, PROGET,
    // DATICUP e G_PEREMESSI bisogna solo effettuare l'update del campo codlav,
    // il quale non è campo chiave ma è solo campo per il collegamento con il
    // lavoro associato. Di tali campo non bisogna copiare i campi del
    // generatore e i documenti associati

    for (int i = 0; i < entita.length; i++) {
      String campoCodlav = "codlav";
      boolean copiaOggetti = true;

      if (entita[i].indexOf('.') >= 0) {
        campoCodlav = entita[i].substring(entita[i].indexOf('.') + 1);
        entita[i] = entita[i].substring(0, entita[i].indexOf('.'));
        copiaOggetti = false;
      }

      if (this.geneManager.getSql().isTable(entita[i])) {
        if (copiaOggetti) {
          // Eseguo la copia delle occorrenze del generatore attributi
          this.geneManager.copiaOccorrenzeGeneratoreAttributi(entita[i], "codlav = ?", new Object[] { sorgente },
              new String[] { "CODLAV" }, new Object[] { destinazione }, true);
          // Eseguo la copia dei documenti associati
          this.geneManager.copiaOccorrenzeOggettiAssociati(entita[i], "CODLAV = ? ", new Object[] { sorgente }, new String[] { "CODLAV" },
              new Object[] { destinazione }, true);
          this.geneManager.copiaOccorrenzeNoteAvvisi(entita[i], "CODLAV = ? ", new Object[] { sorgente }, new String[] { "CODLAV" },
              new Object[] { destinazione }, true);
        }

        // Ora eseguo il vero e proprio update
        StringBuffer update = new StringBuffer("update ");
        update.append(entita[i]);
        update.append(" set ");
        update.append(campoCodlav);
        update.append(" = ? where ");
        update.append(campoCodlav);
        update.append(" = ? ");

        try {
          this.geneManager.getSql().update(update.toString(), new Object[] { destinazione, sorgente });
        } catch (SQLException e) {
          throw new GestoreException("Errore nella modifica del codice per la tabella " + entita[i], "rinominaLavoro", e);
        }
      }
    }
    // Eseguo l'update della storia modifiche
    if (this.geneManager.getSql().isTable("ST_TRG")) {
      try {
        long max = this.geneManager.getLongFromSelect("select max(st_seq) from st_trg where st_table='PERI' and st_operation = 'INS' and "
            + "st_val1=?", new Object[] { sorgente }, true);
        this.geneManager.getSql().update("update st_trg set st_val1= ? where st_val1 = ? and st_key1='CODLAV' " + "and st_seq >= ?",
            new Object[] { destinazione, sorgente, new Long(max) });
      } catch (SQLException e) {
        throw new GestoreException("Errore nella modifica del codice per la tabella della storia modifiche", "rinominaLavoro", e);
      }
    }

  }

  /**
   * Creazione di un lavoro di livello superiore a partire da un lavoro sorgente
   *
   * @param codLavSorgente
   * @param codLavDestinazione
   * @param livelloLavoroDestinazione
   * @throws GestoreException
   */
  public void creaLavoroDiLivelloSuccessivo(TransactionStatus status, HttpServletRequest request) throws GestoreException {

    String codLavSorgente = UtilityStruts.getParametroString(request, "SORGENTE");

    String codLavDestinazione = "";

    if (this.isCodificaAutomaticaMIT()) {
      String annoOpera = UtilityStruts.getParametroString(request, "ANNOOPERA");
      String amministrazioneRiferimento = UtilityStruts.getParametroString(request, "AMMINISTRAZIONERIFERIMENTO");
      String regione = UtilityStruts.getParametroString(request, "REGIONE");
      String numeroProgressivo = UtilityStruts.getParametroString(request, "NUMEROPROGRESSIVO");
      codLavDestinazione = this.calcolaCodificaAutomaticaMIT(annoOpera, amministrazioneRiferimento, regione, numeroProgressivo);
    } else if (geneManager.isCodificaAutomatica("PERI", "CODLAV")) {
      codLavDestinazione = geneManager.calcolaCodificaAutomatica("PERI", "CODLAV");
    } else {
      codLavDestinazione = UtilityStruts.getParametroString(request, "DESTINAZIONE");
    }

    String livelloLavoroDestinazione = UtilityStruts.getParametroString(request, "LIVELLO_DESTINAZIONE");
    String archiviaSorgente = UtilityStruts.getParametroString(request, "ARCHIVIA_SORGENTE");

    // Come prima cosa verifico la non esistenza del codice lavoro di
    // destinazione
    try {
      List ret = this.geneManager.getSql().getVector("select 1 from peri where codlav = ?", new Object[] { codLavDestinazione });
      if (ret != null && ret.size() > 0)
        throw new GestoreException("Il lavoro di destinazione è già esistente !", "creaLavoroLivelloSuperiore.destinazioneEsistente");
    } catch (SQLException e) {
      throw new GestoreException("Errore nella selezione del lavoro di destinazione !", "creaLavoroLivelloSuperiore", e);
    }
    // Come prima cosa verifico l'esistenza del codice lavoro sorgente
    try {
      List ret = this.geneManager.getSql().getVector("select 1 from peri where codlav = ?", new Object[] { codLavSorgente });
      if (ret == null || ret.size() == 0)
        throw new GestoreException("Il lavoro sorgente non esiste", "creaLavoroLivelloSuperiore.sorgenteNonEsistente");
    } catch (SQLException e) {
      throw new GestoreException("Errore nella selezione del lavoro sorgente !", "creaLavoroLivelloSuperiore", e);
    }

    // Lista delle entita' da copiare. Le entita' APPA e CONT sono state
    // inserite
    // per effettuarne l'inizializzazione (di conseguenza anche l'entita' CONT)
    String entita[] = { "PERI", "APPR", "G2TECN", "CDCO", "DATICUP", "APPA", "CONT" };

    for (int i = 0; i < entita.length; i++) {
      if (this.geneManager.getSql().isTable(entita[i])
          && this.geneManager.countOccorrenze(entita[i], "codlav = ?", new Object[] { codLavSorgente }) > 0) {
        StringBuffer sql = new StringBuffer("select * from ");
        sql.append(entita[i]);
        sql.append(" where codlav = ?");
        // Se ci sono occorenze allora eseguo la copia
        DataColumnContainer impl = new DataColumnContainer(this.geneManager.getSql(), entita[i], sql.toString(),
            new Object[] { codLavSorgente });
        List dati = null;
        try {
          dati = this.geneManager.getSql().getListHashMap(sql.toString(), new Object[] { codLavSorgente });
          if (dati != null && dati.size() > 0) {
            for (int row = 0; row < dati.size(); row++) {
              impl.setValoriFromMap((HashMap) dati.get(row), true);
              impl.setValue("CODLAV", codLavDestinazione);
              // Gestioni particolari della creazione del lavoro di livello
              // successivo
              if ("PERI".equalsIgnoreCase(entita[i])) {
                // Lavoro (PERI): 3 fasi:
                // 1. update dei campi LIVPRO
                switch (impl.getDouble("PERI.LIVPRO").intValue()) {
                case 0:
                  impl.setValue("PERI.CODSTF", codLavSorgente);
                  break;
                case 1:
                  impl.setValue("PERI.CODPRG", codLavSorgente);
                  break;
                case 2:
                  impl.setValue("PERI.PCODPR", codLavSorgente);
                  break;
                }
                impl.setValue("PERI.NPRLAV",
                    codLavDestinazione.substring(0, codLavDestinazione.length() > 10 ? 9 : codLavDestinazione.length())); // ritorno
                                                                                                                          // al
                                                                                                                          // massimo
                                                                                                                          // i
                                                                                                                          // primi
                                                                                                                          // 10
                                                                                                                          // caratteri
                impl.setValue("PERI.LIVPRO", new Long(livelloLavoroDestinazione));

                // 2. sbianco tutti i campi di importo di PERI. La lista dei
                // campi importo di seguito definita e' stata copiata dal codice
                // sorgente di PWB che realizza la stessa funzionalita'
                String[] arrayCampiImportoPERI = new String[] { "ilabac", "itsadc", "itoc", "sompc1", "sompc2", "sompc3", "sompc4",
                    "sompc5", "sompc6", "sompc7", "sompc8", "sompc9", "sompc10", "sompc11", "sompc12", "sompc13", "sompc14", "ipg_ilabac",
                    "ipg_itsadc", "ipg_itoc", "ipg_sompc1", "ipg_sompc2", "ipg_sompc3", "ipg_sompc4", "ipg_sompc5", "ipg_sompc6",
                    "ipg_sompc7", "ipg_sompc8", "ipg_sompc9", "ipg_sompc10", "ipg_sompc11", "ipg_sompc12", "ipg_sompc13", "ipg_sompc14",
                    "imppro", "ilafim", "ilafic", "ilafie", "ilafis", "ilafism", "ilafisc", "ilafise", "ipg_lavm", "ipg_lavc", "ipg_lave",
                    "ipg_lavs", "ipg_lvsm", "ipg_lvsc", "ipg_lvse" };

                for (int j = 0; j < arrayCampiImportoPERI.length; j++)
                  impl.setValue("PERI.".concat(arrayCampiImportoPERI[j].toUpperCase()), null);

                // 3. esecuzione dell'insert del nuovo record in PERI
                impl.insert(entita[i], this.geneManager.getSql());

                // Creazione permessi del nuovo lavoro a partire dai permessi
                // del lavoro sorgente
                AbstractGestoreEntita gest = new DefaultGestoreEntita("PERI", request);
                gest.inserisciPermessi(impl, "CODLAV", new Integer(1));

                // Update del flag 'Livello di progettazione superato' nel
                // progetto sorgente
                this.geneManager.getSql().update("update PERI set LIVSUP = 1 where codlav = ?", new Object[] { codLavSorgente });

                if (archiviaSorgente != null && archiviaSorgente.length() > 0) {
                  // Update del campo ISARCHI del lavoro sorgente
                  this.geneManager.getSql().update("update PERI set ISARCHI = " + archiviaSorgente + " where codlav = ?",
                      new Object[] { codLavSorgente });
                }

                // Eseguo la copia delle occorrenze del generatore attributi
                this.geneManager.copiaOccorrenzeGeneratoreAttributi(entita[i], "codlav = ?", new Object[] { codLavSorgente },
                    new String[] { "CODLAV" }, new Object[] { codLavDestinazione }, false);

              } else if ("APPA".equalsIgnoreCase(entita[i])) {
                // Appalto (APPA): inizializzazione dell'appalto
                if (impl.getLong("APPA.NAPPAL").intValue() == 1) {
                  // Determino campi utili per l'inizializzazione dell'appalto
                  HashMap map = this.geneManager.getSql().getHashMap(
                      "select TITSIL, CODPROE, INTERV, CATINT1, CLAINT, SETINT, TIPOPR " + "from PERI where CODLAV = ?",
                      new Object[] { codLavSorgente });

                  JdbcParametro titoloAppalto = ((JdbcParametro) map.get("TITSIL"));
                  JdbcParametro ivaAppalto = ((JdbcParametro) map.get("CODPROE"));
                  this.geneManager.getSql().update(
                      "insert into APPA (CODLAV, NAPPAL, TIAPPA, NOTAPP, "
                          + "RITRP, ALEARP, RITINF, PERREA, IVALAV, IVARP, TIPLAVG) "
                          + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                      new Object[] { codLavDestinazione, new Long(1), new Long(1), titoloAppalto.getValue(), new Long(15), new Long(10),
                          new Double(0.5), new Long(10), ivaAppalto.getValue(), ivaAppalto.getValue(), new Long(1) });

                  // UPDATE DELLA CATEGORIA INTERVENTO PER LE ENTITA COLLEGATE
                  // per il lavoro destinazione
                  this.updateCatintInAppaOrdi(codLavDestinazione, ((JdbcParametro) map.get("INTERV")).getStringValue(),
                      ((JdbcParametro) map.get("CATINT1")).getStringValue(), ((JdbcParametro) map.get("CLAINT")).getStringValue(),
                      ((JdbcParametro) map.get("SETINT")).getStringValue(), ((JdbcParametro) map.get("TIPOPR")).getStringValue());
                }
              } else if ("CONT".equalsIgnoreCase(entita[i])) {
                if (row == 0) {
                  // Contratto (CONT): inizializzazione del contratto
                  HashMap map = this.geneManager.getSql().getHashMap("select CODPROE from PERI where CODLAV = ?",
                      new Object[] { codLavSorgente });
                  JdbcParametro ivaAppalto = ((JdbcParametro) map.get("CODPROE"));

                  impl = null;
                  sql = new StringBuffer("select codlav, nappal, nproat, codapp, neseco "
                      + "from CONT where codlav = ? AND nappal = 1 AND nproat = 1");
                  impl = new DataColumnContainer(this.geneManager.getSql(), entita[i], sql.toString(), new Object[] { codLavSorgente });

                  impl.setValue("CODLAV", codLavDestinazione);
                  impl.setValue("NAPPAL", new Long(1));
                  impl.setValue("NPROAT", new Long(1));
                  impl.setValue("CODAPP", new Long(1));
                  impl.setValue("NESECO", ivaAppalto.getValue());

                  // Insert nuovo record in CONT
                  AbstractGestoreEntita gestoreCONT = UtilityStruts.getGestoreEntita("CONT", request, GestoreCONT.class.getName());

                  gestoreCONT.inserisci(status, impl);
                }
              } else if ("APPR".equalsIgnoreCase(entita[i])) {
                // Bisogna copiare al più un record
                if (row == 0) {
                  Object maxNaprpr = this.geneManager.getSql().getObject(
                      "select max(NAPRPR) from APPR where CODLAV = ? and DDAPPR is not null", new Object[] { codLavSorgente });

                  sql = new StringBuffer("SELECT codlav, naprpr, tipper, supesp, sprog, ilabap, itsadp, itotpr, "
                      + "sompr1, sompr2, sompr3, sompr4, sompr5, sompr6, sompr7, sompr8, "
                      + "iridim, ilabaf, itsomf, itotfi, "
                      + "somfi1, somfi2, somfi3, somfi4, somfi5, somfi6, somfi7, somfi8, "
                      + "ilabav, itsomv, itotfv, "
                      + "somfv1, somfv2, somfv3, somfv4, somfv5, somfv6, somfv7, somfv8, "
                      + "isecde, itofin, lavprm, lavprc, lavpre, lavpro, lavprs, "
                      + "sompr9, sompr10, sompr11, sompr12, somfi9, somfi10, somfi11, somfi12, "
                      + "somfv9, somfv10, somfv11, somfv12, "
                      + "lavfim, lavfic, lavfie, lavfio, lavfis, lavfvm, "
                      + "lavfvc, lavfve, lavfvo, lavfvs, "
                      + "sompr13, somfi13, somfv13, sompr14, somfi14, "
                      + "lavfism, lavfisc, lavfise, lavprsm, lavprsc, lavprse, "
                      + "lavnrm, lavnrc, lavnre, lavnrl, lavprnm, lavprnc, "
                      + "lavprne,lavprnl, ivaappr "
                      + "FROM APPR ");
                  String selectFrom = sql.toString().concat("WHERE codlav = ? ");

                  impl = null;
                  if (maxNaprpr != null) {
                    selectFrom = selectFrom.concat(" AND NAPRPR = ?");
                    impl = new DataColumnContainer(this.geneManager.getSql(), entita[i], selectFrom, new Object[] { codLavSorgente,
                        maxNaprpr });

                    // Array campi importi netti dell'approvazione
                    String[] importiNettiAppr = new String[] { "lavfim", "lavfic", "lavfie", "lavfio", "lavfis", "ilabaf", "somfi1",
                        "somfi2", "somfi3", "somfi4", "somfi5", "somfi6", "somfi7", "somfi8", "somfi9", "somfi10", "somfi11", "somfi12",
                        "somfi13", "somfi14", "itsomf", "itotfi", "lavfism", "lavfisc", "lavfise", "lavnrm", "lavnrc", "lavnre", "lavnrl" };
                    // Array campi degli importi lordi dell'approvazione
                    String[] importiLordiAppr = new String[] { "lavprm", "lavprc", "lavpre", "lavpro", "lavprs", "ilabap", "sompr1",
                        "sompr2", "sompr3", "sompr4", "sompr5", "sompr6", "sompr7", "sompr8", "sompr9", "sompr10", "sompr11", "sompr12",
                        "sompr13", "sompr14", "itsadp", "itotpr", "lavprsm", "lavprsc", "lavprse", "lavprnm", "lavprnc", "lavprne",
                        "lavprnl" };

                    for (int l = 0; l < importiNettiAppr.length; l++)
                      impl.setValue(importiLordiAppr[l], impl.getDouble(importiNettiAppr[l]));

                    // Lista dei record della SOMM da copiare
                    List listaSOMM = this.geneManager.getSql().getListVector(
                        "select NAPRPR, NSORIF, NSOMMA, DESOMM, IMSOMM, IMSOMP, IMSOMV, IVASOM from SOMM where CODLAV = ? and NAPRPR = ?",
                        new Object[] { codLavSorgente, maxNaprpr });

                    if (listaSOMM != null && listaSOMM.size() > 0) {
                      for (int j = 0; j < listaSOMM.size(); j++) {
                        Vector recordSOMM = (Vector) listaSOMM.get(j);

                        // Copia dell'i-esima occorrenza della tabella SOMM
                        this.geneManager.getSql().update(
                            "insert into SOMM (CODLAV, NAPRPR, NSORIF, NSOMMA, DESOMM, IMSOMM, IMSOMP, IMSOMV, IVASOM) values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            new Object[] {
                                codLavDestinazione,
                                new Long(1),
                                SqlManager.getValueFromVectorParam(recordSOMM, 1).getValue(),
                                SqlManager.getValueFromVectorParam(recordSOMM, 2).getValue(),
                                SqlManager.getValueFromVectorParam(recordSOMM, 3).getValue(),
                                SqlManager.getValueFromVectorParam(recordSOMM, 4).getValue(),
                                SqlManager.getValueFromVectorParam(recordSOMM, 4).getValue(),// importoNettoApprPrec
                                                                                             // !=
                                                                                             // null
                                                                                             // ?
                                                                                             // SqlManager.getValueFromVectorParam(importoNettoApprPrec,
                                                                                             // 0).getValue()
                                                                                             // :
                                                                                             // SqlManager.getValueFromVectorParam(listaSOMM.get(j),
                                                                                             // 5).getValue(),
                                SqlManager.getValueFromVectorParam(recordSOMM, 6).getValue(),
                                SqlManager.getValueFromVectorParam(recordSOMM, 7).getValue() });
                      }
                    }
                  } else {
                    // In questo caso si effettua inizialiazzazione della
                    // tabella APPR
                    selectFrom = "SELECT codlav, naprpr, tipper FROM appr WHERE codlav = ? and naprpr = ?";
                    impl = new DataColumnContainer(this.geneManager.getSql(), entita[i], selectFrom, new Object[] { codLavSorgente,
                        new Long(1) });
                  }

                  // Set di APPR.NAPRPR
                  impl.setValue("codlav", codLavDestinazione);
                  impl.setValue("naprpr", new Long(1));
                  // Set di APPR.TIPPER
                  impl.setValue("tipper", new Long(1));

                  // Inserimento del nuovo record in appr
                  AbstractGestoreEntita gestoreAPPR = UtilityStruts.getGestoreEntita("APPR", request, GestoreAPPR.class.getName());

                  gestoreAPPR.inserisci(status, impl);

                  if (maxNaprpr != null) {
                    // Eseguo la copia delle occorrenze del generatore attributi
                    this.geneManager.copiaOccorrenzeGeneratoreAttributi(entita[i], "codlav = ? and naprpr = ? ", new Object[] {
                        codLavSorgente, maxNaprpr }, new String[] { "CODLAV", "NAPRPR" }, new Object[] { codLavDestinazione, new Long(1) },
                        false);
                  }
                }
              } else if ("G2TECN".equalsIgnoreCase(entita[i])) {
                // Esecuzione dell'insert del nuovo record in G2TECN
                impl.insert(entita[i], this.geneManager.getSql());
              } else if ("CDCO".equalsIgnoreCase(entita[i])) {
                // Esecuzione dell'insert del nuovo record in CDCO
                impl.insert(entita[i], this.geneManager.getSql());
              } else if ("DATICUP".equalsIgnoreCase(entita[i])) {

                Long maxVal = new Long(this.geneManager.getLongFromSelect("select max(numpro) from daticup", null, false));

                maxVal = new Long(maxVal.longValue() + 1);
                impl.setValue("NUMPRO", maxVal);

                // Esecuzione dell'insert del nuovo record in DATICUP
                impl.insert(entita[i], this.geneManager.getSql());
              }
            }
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nella creazione del lavoro di livello successivo per " + "entità " + entita[i],
              "creaLavoroLivelloSuccessivo", e);
        }
      }
    }
  }

  /**
   * Funzione per determinare il SAL corrente e' l'ultimo SAL chiuso
   *
   * @param codLav
   * @param nappal
   * @param numSal
   * @return Ritorna true se il SAL corrente e' l'ultimo SAL chiuso, false
   *         altrimenti
   * @throws GestoreException
   */
  public boolean isSALCorrenteUltimoSALChiuso(String codLav, long nappal, long numSal) throws GestoreException {
    boolean salCorrenteChiuso = false;
    boolean salPrecedentiChiusi = false;
    boolean salSuccessiviNonChiusi = false;

    try {
      // Numero di record in psal relativi al lavoro in analisi
      Long numeroSAL = ((Long) this.geneManager.getSql().getObject("select count(numsal) from psal where codlav = ? and nappal = ?",
          new Object[] { codLav, new Long(nappal) }));

      if (numSal > 0) {
        List ret = null;
        // Determino lo stato del SAL corrente: e' chiuso oppure no.
        String saluff = (String) this.geneManager.getSql().getObject(
            "select saluff from psal where codlav = ? and nappal = ? and numsal = ?",
            new Object[] { codLav, new Long(nappal), new Long(numSal) });

        if ("1".equals(saluff)) salCorrenteChiuso = true;

        if (numSal > 1) {
          ret = this.geneManager.getSql().getListVector(
              "select 1 from psal where codlav = ? and nappal = ? and numsal < ? and saluff = '1'",
              new Object[] { codLav, new Long(nappal), new Long(numSal) });
          if (ret != null && ret.size() > 0 && ret.size() == (numSal - 1)) salPrecedentiChiusi = true;
        } else {
          salPrecedentiChiusi = true;
        }

        ret = null;
        if (numSal < numeroSAL.longValue()) {
          ret = this.geneManager.getSql().getListVector(
              "select 1 from psal where codlav = ? and nappal = ? and numsal > ? and (saluff <> '1' or saluff is null)",
              new Object[] { codLav, new Long(nappal), new Long(numSal) });
          if (ret != null && ret.size() > 0 && ret.size() == (numeroSAL.longValue() - numSal)) salSuccessiviNonChiusi = true;
        } else {
          salSuccessiviNonChiusi = true;
        }
      } else {
        // Se numSal < 0 significa che si sta richiedendo la creazione di un
        // nuovo SAL, quindi il campo denominato 'SAL chiuso?' deve essere
        // modificabile se il SAL precedente è chiuso. Il metodo quindi deve
        // tornare true se il SAL precedente è chiuso, false altrimenti
        if (numeroSAL != null && numeroSAL.longValue() > 0) {
          // Nella base dati esistono dei SAL relativi al lavoro in analisi
          // Determino lo stato dell'ultimo SAL inserito
          String saluff = (String) this.geneManager.getSql().getObject(
              "select saluff from psal where codlav = ? and nappal = ? and numsal = ?",
              new Object[] { codLav, new Long(nappal), numeroSAL });
          if ("1".equals(saluff)) {
            salCorrenteChiuso = true;
            salPrecedentiChiusi = true;
            salSuccessiviNonChiusi = true;
          } else {
            salCorrenteChiuso = false;
            salPrecedentiChiusi = false;
            salSuccessiviNonChiusi = false;
          }
        } else {
          salCorrenteChiuso = true;
          salPrecedentiChiusi = true;
          salSuccessiviNonChiusi = true;
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nel determinare se il SAL corrente (numsal = " + numSal + ") e' l'ultimo SAL chiuso", null, e);
    }
    return salCorrenteChiuso && salPrecedentiChiusi && salSuccessiviNonChiusi;
  }

  /**
   * Funzione per determinare il SAL corrente e' il primo SAL non aperto
   *
   * @param codLav
   * @param nappal
   * @param numSal
   * @return Ritorna true se il SAL corrente e' il primo SAL non chiuso, false
   *         altrimenti
   * @throws GestoreException
   */
  public boolean isSALCorrentePrimoSALNonChiuso(String codLav, long nappal, long numSal) throws GestoreException {
    boolean salCorrenteNonChiuso = false;
    boolean salPrecedentiChiusi = false;
    boolean salSuccessiviNonChiusi = false;

    try {
      // Numero di record in psal relativi al lavoro in analisi
      Long numeroSAL = ((Long) this.geneManager.getSql().getObject("select count(numsal) from psal where codlav = ? and nappal = ?",
          new Object[] { codLav, new Long(nappal) }));

      if (numSal > 0) {
        List ret = null;
        // Determino lo stato del SAL corrente: e' chiuso oppure no.
        String saluff = (String) this.geneManager.getSql().getObject(
            "select saluff from psal where codlav = ? and nappal = ? and numsal = ?",
            new Object[] { codLav, new Long(nappal), new Long(numSal) });

        if (!"1".equals(saluff)) salCorrenteNonChiuso = true;

        if (numSal > 1) {
          ret = this.geneManager.getSql().getListVector(
              "select 1 from psal where codlav = ? and nappal = ? and numsal < ? and saluff = '1'",
              new Object[] { codLav, new Long(nappal), new Long(numSal) });
          if (ret != null && ret.size() > 0 && ret.size() == (numSal - 1)) salPrecedentiChiusi = true;
        } else {
          salPrecedentiChiusi = true;
        }

        ret = null;
        if (numSal < numeroSAL.longValue()) {
          ret = this.geneManager.getSql().getListVector(
              "select 1 from psal where codlav = ? and nappal = ? and numsal > ? and (saluff <> '1' or saluff is null)",
              new Object[] { codLav, new Long(nappal), new Long(numSal) });
          if (ret != null && ret.size() > 0) salSuccessiviNonChiusi = true;
        } else {
          salSuccessiviNonChiusi = true;
        }
      } else {
        // Se numSal < 0 significa che si sta richiedendo la creazione di un
        // nuovo SAL, quindi il campo denominato 'SAL chiuso?' deve essere
        // modificabile solo se il l'ultimo SAL inserito e' chiuso.
        // Se si sta creando il primo SAL il campo sara' modificabile
        if (numeroSAL != null && numeroSAL.longValue() > 0) {
          // Nella base dati esistono dei SAL relativi al lavoro in analisi
          // Determino lo stato dell'ultimo SAL inserito
          String saluff = (String) this.geneManager.getSql().getObject(
              "select saluff from psal where codlav = ? and nappal = ? and numsal = ?",
              new Object[] { codLav, new Long(nappal), numeroSAL });
          if ("1".equals(saluff)) {
            salCorrenteNonChiuso = true;
            salPrecedentiChiusi = true;
            salSuccessiviNonChiusi = true;
          } else {
            salCorrenteNonChiuso = false;
            salPrecedentiChiusi = false;
            salSuccessiviNonChiusi = false;
          }
        } else {
          salCorrenteNonChiuso = true;
          salPrecedentiChiusi = true;
          salSuccessiviNonChiusi = true;
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nel determinare se il SAL corrente (numsal = " + numSal + ") e' il primo SAL non chiuso", null, e);
    }
    return salCorrenteNonChiuso && salPrecedentiChiusi && salSuccessiviNonChiusi;
  }

  /**
   * Determina l'importo di iscrizione associato al numero di categoria. Questa
   * funzione viene utilizzata dalle classi GestoreCATAPP e
   * GestoreULTAPPMultiplo per il salvataggio della categoria prevalente e delle
   * categorie ulteriori presenti nella pagina dell'appalto del contratto
   *
   * @param tipoCategoria
   *        Tipo di categoria: 1=Lavori, 2=Forniture, 3=Servizi
   * @param numeroClassifica
   * @return Ritorna l'importo d'iscrizione associato al numero di categoria
   * @throws GestoreException
   */
  public Double getImportoIscrizioneCategoria(int tipoCategoria, int numeroClassifica) throws GestoreException {
    String codiceTabellato = null;
    switch (tipoCategoria) {
    case 1:
      codiceTabellato = "G_z09";
      break;
    case 2:
      codiceTabellato = "G_z07";
      break;
    case 3:
      codiceTabellato = "G_z08";
      break;
    }

    String importoIscrizione = null;
    try {
      importoIscrizione = (String) this.geneManager.getSql().getObject("select tab2d1 from tab2 where tab2cod = ? and tab2tip = ?",
          new Object[] { codiceTabellato, UtilityNumeri.convertiIntero(new Integer(numeroClassifica)) });
    } catch (SQLException e) {
      throw new GestoreException("Errore nel determinare l'importo d'iscrizione della categoria dell'appalto", null, e);
    }
    return UtilityNumeri.convertiDouble(importoIscrizione);
  }

  /**
   * Determina il massimo valore del campo SUBA.NUMPRS, filtrando per codice
   * lavoro e numero appalto
   *
   * @param codiceLavoro
   * @param numeroAppalto
   * @return Ritorna il massimo valore del campo SUBA.NUMPRS filtrando per
   *         codice lavoro e numero appalto. Se non esiste nessun record, allora
   *         ritorna 0
   *
   * @throws GestoreException
   */
  public int getMaxSubAppalto(String codiceLavoro, Long numeroAppalto) throws GestoreException {
    int result = 0;
    String sql = "select max(numprs) from suba where codlav = ? and nappal = ?";
    try {
      Object obj = this.geneManager.getSql().getObject(sql, new Object[] { codiceLavoro, numeroAppalto });
      if (obj != null) result = ((Long) obj).intValue();
    } catch (SQLException e) {
      throw new GestoreException("Errore nel determinare il numero di subappalti esistenti nella base "
          + "dati associati al lavoro '"
          + codiceLavoro
          + "'", null, e);
    }
    return result;
  }

  /**
   * Aggiornamento del campo SUBA.ASSSUB (Assimilabile a subappalto) di tutti i
   * subappalti di tipo Nolo o Fornitura dell'i'esimo appalto del lavoro in
   * analisi
   *
   * @param status
   * @param impl
   * @throws GestoreException
   */
  public int aggiornaAssimilabileSubNoliForniture(String codiceLavoro, int numeroAppalto, Long numeroProgressivo, String codiceImpresa,
      String assimilabileSubappalto) throws GestoreException {
    int result = -1;

    String sql = "update SUBA set ASSSUB = ? "
        + "where CODLAV = ? "
        + "and NAPPAL = ? "
        + "and CODSUB = ? "
        + "and TIPO is not null "
        + "and TIPO not in (0, 1) "
        + "and ASSSUB <> ? ";
    Object[] paramSql = null;

    if (numeroProgressivo != null) {
      paramSql = new Object[6];
      paramSql[0] = assimilabileSubappalto;
      paramSql[1] = codiceLavoro;
      paramSql[2] = new Long(numeroAppalto);
      paramSql[3] = codiceImpresa;
      paramSql[4] = assimilabileSubappalto;
      paramSql[5] = numeroProgressivo;
      sql = sql.concat("and NUMPRS <> ?");
    }

    try {
      result = this.geneManager.getSql().update(sql, paramSql);
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento del campo SUBA.ASSSUB dei subappalti "
          + "esistenti di tipo diverso da Subappalto associati all'appalto "
          + "numero "
          + numeroAppalto
          + " del lavoro '"
          + codiceLavoro
          + "'", null, e);
    }
    return result;
  }

  /**
   * Metodo per la gestione del collegamento dell'appalto alla gara
   *
   * @param impl
   * @throws GestoreException
   */
  public void gestioneCollegamentoGara(DataColumnContainer impl) throws GestoreException {
    if (this.geneManager.esisteTabella("GARE")) {
      if (impl.isColumn("GARE.NGARA")) {
        DataColumn campoNgara = impl.getColumn("GARE.NGARA");
        if (campoNgara.isModified()) {
          String ngara_new = campoNgara.getValue().getStringValue();
          String ngara_old = campoNgara.getOriginalValue().getStringValue();

          //String selectGARE = "select gare.codgar1, v_gare_torn.genere, gare.ditta from gare, v_gare_torn where gare.codgar1 = v_gare_torn.codgar and ngara = ?";
          String selectGARE = "select gare.codgar1, v_gare_torn.genere, gare.ditta, torn.modcont from gare, v_gare_torn, torn where gare.codgar1 = v_gare_torn.codgar "
              + "and gare.ngara = ? and torn.codgar= v_gare_torn.codgar";
          String updateGAREGenere1_2 = "update gare set clavor = ?, numera = ? where ngara = ?";
          String updateGAREGenere3 = "update gare set clavor = ?, numera = ? where codgar1 = ? and ditta = ? and genere is null";

          try {
            String codgar1_new = null;
            Long genere_new = null;
            String ditta_new = null;
            Long modcont = null;
            if (!"".equals(ngara_new)) {
              List<?> datiGARA = this.geneManager.getSql().getVector(selectGARE, new Object[] { ngara_new });
              if (datiGARA != null && datiGARA.size() > 0) {
                codgar1_new = (String) SqlManager.getValueFromVectorParam(datiGARA, 0).getValue();
                genere_new = (Long) SqlManager.getValueFromVectorParam(datiGARA, 1).getValue();
                ditta_new = (String) SqlManager.getValueFromVectorParam(datiGARA, 2).getValue();
                modcont = (Long) SqlManager.getValueFromVectorParam(datiGARA, 3).getValue();
              }
            }

            String codgar1_old = null;
            Long genere_old = null;
            String ditta_old = null;
            if (!"".equals(ngara_old)) {
              List<?> datiGARA = this.geneManager.getSql().getVector(selectGARE, new Object[] { ngara_old });
              if (datiGARA != null && datiGARA.size() > 0) {
                codgar1_old = (String) SqlManager.getValueFromVectorParam(datiGARA, 0).getValue();
                genere_old = (Long) SqlManager.getValueFromVectorParam(datiGARA, 1).getValue();
                ditta_old = (String) SqlManager.getValueFromVectorParam(datiGARA, 2).getValue();
              }
            }

            String codlav = impl.getString("APPA.CODLAV");
            Long nappal = impl.getLong("APPA.NAPPAL");

            // Per ognuna delle operazioni seguenti è necessario controllare
            // prima se la gara in esame è di tipo 1 o 2 (divisa in lotti o
            // lotto singolo) oppure 3 (divisa in lotti con offerta unica). In
            // quest'ultimo caso l'aggiornamento dell'entità GARE non puo'
            // essere effettuato solamente considerando il filtro sulla gara
            // stessa (GARA.NGARA), ma è necessario considerare tutte le gare
            // (lotti) vinti dalla ditta assegnataria del contratto.

            if (ngara_new == "") {
              // Sbianco il riferimento OLD alla gara
              if (genere_old != null && new Long(3).equals(genere_old) && new Long(2).equals(modcont)) {
                this.geneManager.getSql().update(updateGAREGenere3, new Object[] { null, null, codgar1_old, ditta_old });
              } else {
                this.geneManager.getSql().update(updateGAREGenere1_2, new Object[] { null, null, ngara_old });
              }
            } else {
              if (ngara_old == "") {
                // Inserimento del riferimento NEW alla gara
                if (genere_new != null && new Long(3).equals(genere_new) && new Long(2).equals(modcont)) {
                  this.geneManager.getSql().update(updateGAREGenere3, new Object[] { codlav, nappal, codgar1_new, ditta_new });
                } else {
                  this.geneManager.getSql().update(updateGAREGenere1_2, new Object[] { codlav, nappal, ngara_new });
                }
              } else {
                // Cambio del riferimento alla gara Sbianco i campi CLAVOR e
                // NUMERA della gara OLD a cui l'appalto era collegato
                if (genere_old != null && new Long(3).equals(genere_old) && new Long(2).equals(modcont)) {
                  this.geneManager.getSql().update(updateGAREGenere3, new Object[] { null, null, codgar1_old, ditta_old });
                } else {
                  this.geneManager.getSql().update(updateGAREGenere1_2, new Object[] { null, null, ngara_old });
                }
                // Inserimento del riferimento alla gara NEW
                if (genere_new != null && new Long(3).equals(genere_new) && new Long(2).equals(modcont)) {
                  this.geneManager.getSql().update(updateGAREGenere3, new Object[] { codlav, nappal, codgar1_new, ditta_new });
                } else {
                  this.geneManager.getSql().update(updateGAREGenere1_2, new Object[] { codlav, nappal, ngara_new });
                }
              }
            }
          } catch (SQLException s) {
            throw new GestoreException("Errore nell'aggiornamento del " + "collegamento dell'appalto alla gara", null, s);
          }
        }
      }
    }
  }

  /**
   * Metodo per cancellare, se esiste, il collegamento gara-appalto a partire da
   * codice lavoro e numero appalto dell'appalto in analisi
   *
   * @param codiceLavoro
   * @param numeroAppalto
   * @throws GestoreException
   */
  public void deleteCollegamentoGara(String codiceLavoro, Long numeroAppalto) throws GestoreException {
    try {
      this.geneManager.getSql().update("update GARE set CLAVOR = ?, NUMERA = ? where CLAVOR = ? and NUMERA = ?",
          new Object[] { null, null, codiceLavoro, numeroAppalto });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione del collegamento alla gara", null, e);
    }
  }

  /**
   * Aggiornamento degli importi "Disponibilità Finanziaria" del piano come
   * somma di tutti i parziali degli interventi contenuti nel piano
   *
   * @param contri
   * @throws GestoreException
   */
  public void aggiornaCostiPiatri(Long contri) throws GestoreException {

    String updateCostiPiatri = "UPDATE PIATRI SET DV1TRI =?, DV2TRI =?, DV3TRI =?, "
        + "IM1TRI =?, IM2TRI =?, IM3TRI =?, MU1TRI =?, MU2TRI =?, MU3TRI =?, "
        + "PR1TRI =?, PR2TRI =?, PR3TRI =?, AL1TRI =?, AL2TRI =?, AL3TRI =?, BI1TRI =?, "
        + "BI2TRI =?, BI3TRI =?, TO1TRI =?, TO2TRI =?, TO3TRI =? WHERE CONTRI =?";

    try {
      TransactionStatus status = this.geneManager.getSql().startTransaction();
      this.geneManager.getSql().update(
          updateCostiPiatri,
          new Object[] { sommaValori("DV1TRI", contri), sommaValori("DV2TRI", contri), sommaValori("DV3TRI", contri),
              sommaValori("IM1TRI", contri), sommaValori("IM2TRI", contri), sommaValori("IM3TRI", contri), sommaValori("MU1TRI", contri),
              sommaValori("MU2TRI", contri), sommaValori("MU3TRI", contri), sommaValori("PR1TRI", contri), sommaValori("PR2TRI", contri),
              sommaValori("PR3TRI", contri), sommaValori("AL1TRI", contri), sommaValori("AL2TRI", contri), sommaValori("AL3TRI", contri),
              sommaValori("BI1TRI", contri), sommaValori("BI2TRI", contri), sommaValori("BI3TRI", contri), sommaValori("DI1INT", contri),
              sommaValori("DI2INT", contri), sommaValori("DI3INT", contri), contri });
      this.geneManager.getSql().commitTransaction(status);
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il ricalcolo degli importi della disponibilità finanziaria del Piano Triennale",
          "interventipianotriennale.ricalcoloCostiPiano", e);
    }
  }

  /**
   * Somma dei valore degli importi del campo indicato per tutti gli interventi
   * associato al piano (PIATRI.CONTRI)
   *
   * @param campo
   * @param contri
   * @return
   * @throws GestoreException
   */
  private Double sommaValori(String campo, Long contri) throws GestoreException {
    String sqlSelectSomma = "select sum(" + campo + ") from INTTRI where CONTRI =?";

    List listaValoreSomma;
    Double somma;

    try {
      listaValoreSomma = this.geneManager.getSql().getListVector(sqlSelectSomma, new Object[] { contri });
      somma = SqlManager.getValueFromVectorParam(listaValoreSomma.get(0), 0).doubleValue();
    } catch (Exception e) {
      somma = new Double(0);
      throw new GestoreException("Errore durante il ricalcolo degli importi della disponibilità finanziaria del Piano Triennale",
          "interventipianotriennale.ricalcoloCostiPiano", e);
    }
    return somma;
  }

  /**
   * Test per verificare se è abilitata la generazione automatica del codice
   * lavoro secondo le specifiche del provveditorato interregionale per le opere
   * pubbliche del Lazio - Abruzzo - Sardegna
   *
   * @return
   */
  public boolean isCodificaAutomaticaMIT() throws GestoreException {
    boolean result = false;
    try {
      String tab1desc = (String) this.geneManager.getSql().getObject("select tab1desc from tab1 where tab1cod = 'A2197' and tab1tip = 1",
          new Object[] {});
      if (tab1desc != null && tab1desc.startsWith("1")) result = true;
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la verifica dell'attivazione della codifica automatica del lavoro secondo MIT",
          "codificaautomaticalavoro.mit", e);
    }
    return result;
  }

  /**
   * Test per verificare se è abilitata la generazione automatica del codice
   * lavoro secondo le specifiche INPS
   *
   * @return
   * @throws GestoreException
   */
  public boolean isCodificaAutomaticaINPS() throws GestoreException {
    boolean result = false;
    try {
      String tab1desc = (String) this.geneManager.getSql().getObject("select tab1desc from tab1 where tab1cod = 'A2203' and tab1tip = 1",
          new Object[] {});
      if (tab1desc != null && tab1desc.startsWith("1")) result = true;
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la verifica dell'attivazione della codifica automatica del lavoro secondo INPS",
          "codificaautomaticalavoro.inps", e);
    }
    return result;
  }

  /**
   * Calcolo del codice lavoro secondo le specifiche del provveditorato
   * interregionale per le opere pubbliche del Lazio - Abruzzo - Sardegna
   *
   * @param annoOpera
   * @param amministrazioneRiferimento
   * @param regione
   * @param numeroProgressivo
   * @return
   * @throws GestoreException
   */
  public String calcolaCodificaAutomaticaMIT(String annoOpera, String amministrazioneRiferimento, String regione, String numeroProgressivo)
      throws GestoreException {
    String codiceLavoro;
    String numeroLotto = null;

    codiceLavoro = annoOpera;
    codiceLavoro += "/" + amministrazioneRiferimento;
    codiceLavoro += "/" + regione;
    if (numeroProgressivo != null) codiceLavoro += "/" + numeroProgressivo;

    String selectPERI = "select codlav from peri where codlav like ?";

    try {
      if (numeroProgressivo == null) {
        List datiPERI = this.geneManager.getSql().getListVector(selectPERI, new Object[] { annoOpera + "/%" });
        if (datiPERI != null && datiPERI.size() > 0) {

          Long numeroProgressivoTmp = new Long(0);

          for (int i = 0; i < datiPERI.size(); i++) {
            String codiceLavoroTmp = (String) SqlManager.getValueFromVectorParam(datiPERI.get(i), 0).getValue();

            if (codiceLavoroTmp != null && codiceLavoroTmp.length() == 16) {
              if ("/".equals(codiceLavoroTmp.substring(2, 3))
                  && "/".equals(codiceLavoroTmp.substring(6, 7))
                  && "/".equals(codiceLavoroTmp.substring(8, 9))
                  && "/".equals(codiceLavoroTmp.substring(13, 14))) {
                String codiceProgressivo = codiceLavoroTmp.substring(9, 13);
                Long codiceProgressivoMax = new Long(Long.parseLong(codiceProgressivo.trim()));
                if (codiceProgressivoMax.longValue() > numeroProgressivoTmp.longValue()) {
                  numeroProgressivoTmp = new Long(codiceProgressivoMax.longValue());
                }
              }
            }
          }

          numeroProgressivoTmp = new Long(numeroProgressivoTmp.longValue() + 1);

          DecimalFormatSymbols simbols = new DecimalFormatSymbols();
          DecimalFormat decimalFormat = new DecimalFormat("0000", simbols);
          numeroProgressivo = decimalFormat.format(numeroProgressivoTmp.longValue());
          numeroLotto = "00";
        } else {
          numeroProgressivo = "0001";
          numeroLotto = "00";
        }

        codiceLavoro += "/" + numeroProgressivo;

      } else {
        List datiPERI = this.geneManager.getSql().getListVector(selectPERI, new Object[] { codiceLavoro + "%" });
        if (datiPERI != null & datiPERI.size() > 0) {

          Long numeroLottoTmp = new Long(0);

          for (int i = 0; i < datiPERI.size(); i++) {
            String codiceLotto = (String) SqlManager.getValueFromVectorParam(datiPERI.get(i), 0).getValue();
            codiceLotto = codiceLotto.substring(14);
            Long numeroLottoMax = new Long(Long.parseLong(codiceLotto.trim()));
            if (numeroLottoMax.longValue() > numeroLottoTmp.longValue()) {
              numeroLottoTmp = new Long(numeroLottoMax.longValue());
            }
          }

          numeroLottoTmp = new Long(numeroLottoTmp.longValue() + 1);

          DecimalFormatSymbols simbols = new DecimalFormatSymbols();
          DecimalFormat decimalFormat = new DecimalFormat("00", simbols);
          numeroLotto = decimalFormat.format(numeroLottoTmp.longValue());

        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il calcolo del codice lavoro MIT", "initLavoro.calcoloCodiceLavoroMIT", e);
    }

    codiceLavoro += "/" + numeroLotto;
    return codiceLavoro;
  }

  /**
   * Calcolo del codice lavoro secondo le specifiche INPS.
   *
   * @param cenint
   * @param codlav_inps
   * @return
   * @throws GestoreException
   */
  public String calcolaCodificaAutomaticaINPS(String cenint, String codlav_inps) throws GestoreException {

    String codiceLavoro = null;
    Long numeroProgressivoTmp = new Long(0);

    try {
      String selectPERI = "select codlav from peri where codlav like ?";
      List datiPERI = this.geneManager.getSql().getListVector(selectPERI, new Object[] { cenint + "-%" });
      if (datiPERI != null && datiPERI.size() > 0) {
        for (int i = 0; i < datiPERI.size(); i++) {
          String codiceProgressivo = (String) SqlManager.getValueFromVectorParam(datiPERI.get(i), 0).getValue();
          if (codiceProgressivo.length() > 9) {
            codiceProgressivo = codiceProgressivo.substring(4, 9);
          } else {
            codiceProgressivo = codiceProgressivo.substring(4);
          }
          Long numeroProgressivoMax = new Long(Long.parseLong(codiceProgressivo.trim()));
          if (numeroProgressivoMax.longValue() > numeroProgressivoTmp.longValue()) {
            numeroProgressivoTmp = new Long(numeroProgressivoMax.longValue());
          }
        }
      }

      numeroProgressivoTmp = new Long(numeroProgressivoTmp.longValue() + 1);
      DecimalFormatSymbols simbols = new DecimalFormatSymbols();
      DecimalFormat decimalFormat = new DecimalFormat("00000", simbols);

      codiceLavoro = cenint + "-" + decimalFormat.format(numeroProgressivoTmp.longValue());
      if (codlav_inps != null && !"".equals(codlav_inps.trim())) {
        codiceLavoro += "-" + codlav_inps.trim();
      }

    } catch (SQLException e) {
      throw new GestoreException("Errore durante il calcolo del codice lavoro INPS", "initLavoro.calcoloCodiceLavoroINPS", e);
    }
    return codiceLavoro;
  }

}